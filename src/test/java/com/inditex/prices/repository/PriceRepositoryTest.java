package com.inditex.prices.repository;

import com.inditex.prices.repository.entity.PriceEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PriceRepositoryTest {

    @Autowired
    private PriceRepository priceRepository;

    @Test
    void test1_shouldReturnPriceList1_atTenAMOnJune14() {
        Optional<PriceEntity> result = priceRepository.findApplicablePrice(
                1L, 35455L, LocalDateTime.of(2020, 6, 14, 10, 0));

        assertThat(result).isPresent();
        assertThat(result.get().getPriceList()).isEqualTo(1);
        assertThat(result.get().getPrice()).isEqualByComparingTo(new BigDecimal("35.50"));
    }

    @Test
    void test2_shouldReturnPriceList2_atFourPMOnJune14() {
        Optional<PriceEntity> result = priceRepository.findApplicablePrice(
                1L, 35455L, LocalDateTime.of(2020, 6, 14, 16, 0));

        assertThat(result).isPresent();
        assertThat(result.get().getPriceList()).isEqualTo(2);
        assertThat(result.get().getPrice()).isEqualByComparingTo(new BigDecimal("25.45"));
    }

    @Test
    void test3_shouldReturnPriceList1_atNinePMOnJune14() {
        Optional<PriceEntity> result = priceRepository.findApplicablePrice(
                1L, 35455L, LocalDateTime.of(2020, 6, 14, 21, 0));

        assertThat(result).isPresent();
        assertThat(result.get().getPriceList()).isEqualTo(1);
        assertThat(result.get().getPrice()).isEqualByComparingTo(new BigDecimal("35.50"));
    }

    @Test
    void test4_shouldReturnPriceList3_atTenAMOnJune15() {
        Optional<PriceEntity> result = priceRepository.findApplicablePrice(
                1L, 35455L, LocalDateTime.of(2020, 6, 15, 10, 0));

        assertThat(result).isPresent();
        assertThat(result.get().getPriceList()).isEqualTo(3);
        assertThat(result.get().getPrice()).isEqualByComparingTo(new BigDecimal("30.50"));
    }

    @Test
    void test5_shouldReturnPriceList4_atNinePMOnJune16() {
        Optional<PriceEntity> result = priceRepository.findApplicablePrice(
                1L, 35455L, LocalDateTime.of(2020, 6, 16, 21, 0));

        assertThat(result).isPresent();
        assertThat(result.get().getPriceList()).isEqualTo(4);
        assertThat(result.get().getPrice()).isEqualByComparingTo(new BigDecimal("38.95"));
    }

    @Test
    void shouldReturnEmpty_whenNoPriceMatchesApplicationDate() {
        Optional<PriceEntity> result = priceRepository.findApplicablePrice(
                1L, 35455L, LocalDateTime.of(2019, 1, 1, 0, 0));

        assertThat(result).isEmpty();
    }
}
