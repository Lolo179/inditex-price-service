package com.inditex.prices.service;

import com.inditex.prices.exception.PriceNotFoundException;
import com.inditex.prices.repository.PriceRepository;
import com.inditex.prices.repository.entity.PriceEntity;
import com.inditex.prices.service.domain.Currency;
import com.inditex.prices.service.domain.Price;
import com.inditex.prices.service.mapper.PriceEntityMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceServiceImplTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private PriceEntityMapper priceEntityMapper;

    @InjectMocks
    private PriceServiceImpl priceServiceImpl;

    @Test
    void shouldReturnDomainPrice_whenApplicablePriceExists() {
        Long brandId = 1L;
        Long productId = 35455L;
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);

        PriceEntity entity = new PriceEntity();
        Price expected = new Price(productId, brandId, 1,
                LocalDateTime.of(2020, 6, 14, 0, 0),
                LocalDateTime.of(2020, 12, 31, 23, 59, 59),
                new BigDecimal("35.50"), Currency.EUR);

        when(priceRepository.findApplicablePrice(brandId, productId, applicationDate))
                .thenReturn(Optional.of(entity));
        when(priceEntityMapper.toDomain(entity)).thenReturn(expected);

        Price result = priceServiceImpl.findApplicablePrice(brandId, productId, applicationDate);

        assertThat(result).isEqualTo(expected);
        verify(priceRepository).findApplicablePrice(brandId, productId, applicationDate);
        verify(priceEntityMapper).toDomain(entity);
    }

    @Test
    void shouldThrowPriceNotFoundException_whenNoPriceExists() {
        Long brandId = 1L;
        Long productId = 35455L;
        LocalDateTime applicationDate = LocalDateTime.of(2019, 1, 1, 0, 0);

        when(priceRepository.findApplicablePrice(brandId, productId, applicationDate))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> priceServiceImpl.findApplicablePrice(brandId, productId, applicationDate))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessageContaining("brandId=1")
                .hasMessageContaining("productId=35455");
    }

    @Test
    void shouldReturnHigherPriorityPrice_whenMultipleRatesOverlap() {
        Long brandId = 1L;
        Long productId = 35455L;
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 16, 0);

        PriceEntity entity = new PriceEntity();
        Price expected = new Price(productId, brandId, 2,
                LocalDateTime.of(2020, 6, 14, 15, 0),
                LocalDateTime.of(2020, 6, 14, 18, 30),
                new BigDecimal("25.45"), Currency.EUR);

        when(priceRepository.findApplicablePrice(brandId, productId, applicationDate))
                .thenReturn(Optional.of(entity));
        when(priceEntityMapper.toDomain(entity)).thenReturn(expected);

        Price result = priceServiceImpl.findApplicablePrice(brandId, productId, applicationDate);

        assertThat(result.priceList()).isEqualTo(2);
        assertThat(result.price()).isEqualByComparingTo("25.45");
    }
}
