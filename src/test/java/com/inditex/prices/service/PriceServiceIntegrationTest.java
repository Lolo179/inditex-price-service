package com.inditex.prices.service;

import com.inditex.prices.exception.PriceNotFoundException;
import com.inditex.prices.service.domain.Currency;
import com.inditex.prices.service.domain.Price;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Service integration tests against the real H2 in-memory database.
 *
 * Unlike PriceServiceImplTest (Mockito), these tests wire the full Spring context
 * so that PriceServiceImpl, PriceRepository, Hibernate and H2 all participate.
 * data.sql is loaded automatically via spring.sql.init.mode=always.
 * Each test runs inside a transaction that is rolled back after completion.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PriceServiceIntegrationTest {

    @Autowired
    private PriceService priceService;

    // -----------------------------------------------------------------------
    // The 5 required cases from the exercise
    // -----------------------------------------------------------------------

    @Test
    void test1_shouldReturnPriceList1_andPrice35_50_at10AMOnJune14() {
        Price result = priceService.findApplicablePrice(1L, 35455L,
                LocalDateTime.of(2020, 6, 14, 10, 0));

        assertThat(result.priceList()).isEqualTo(1);
        assertThat(result.price()).isEqualByComparingTo(new BigDecimal("35.50"));
        assertThat(result.currency()).isEqualTo(Currency.EUR);
        assertThat(result.brandId()).isEqualTo(1L);
        assertThat(result.productId()).isEqualTo(35455L);
    }

    @Test
    void test2_shouldReturnPriceList2_andPrice25_45_at4PMOnJune14() {
        Price result = priceService.findApplicablePrice(1L, 35455L,
                LocalDateTime.of(2020, 6, 14, 16, 0));

        assertThat(result.priceList()).isEqualTo(2);
        assertThat(result.price()).isEqualByComparingTo(new BigDecimal("25.45"));
    }

    @Test
    void test3_shouldReturnPriceList1_andPrice35_50_at9PMOnJune14() {
        Price result = priceService.findApplicablePrice(1L, 35455L,
                LocalDateTime.of(2020, 6, 14, 21, 0));

        assertThat(result.priceList()).isEqualTo(1);
        assertThat(result.price()).isEqualByComparingTo(new BigDecimal("35.50"));
    }

    @Test
    void test4_shouldReturnPriceList3_andPrice30_50_at10AMOnJune15() {
        Price result = priceService.findApplicablePrice(1L, 35455L,
                LocalDateTime.of(2020, 6, 15, 10, 0));

        assertThat(result.priceList()).isEqualTo(3);
        assertThat(result.price()).isEqualByComparingTo(new BigDecimal("30.50"));
    }

    @Test
    void test5_shouldReturnPriceList4_andPrice38_95_at9PMOnJune16() {
        Price result = priceService.findApplicablePrice(1L, 35455L,
                LocalDateTime.of(2020, 6, 16, 21, 0));

        assertThat(result.priceList()).isEqualTo(4);
        assertThat(result.price()).isEqualByComparingTo(new BigDecimal("38.95"));
    }

    // -----------------------------------------------------------------------
    // Boundary and error cases
    // -----------------------------------------------------------------------

    @Test
    void shouldReturnHigherPriorityPrice_whenTwoRatesOverlap() {
        // 2020-06-14 16:00 falls inside both rate 1 (priority 0) and rate 2 (priority 1)
        // Priority 1 must win
        Price result = priceService.findApplicablePrice(1L, 35455L,
                LocalDateTime.of(2020, 6, 14, 16, 0));

        assertThat(result.priceList()).isEqualTo(2);
    }

    @Test
    void shouldThrowPriceNotFoundException_whenProductDoesNotExist() {
        assertThatThrownBy(() ->
                priceService.findApplicablePrice(1L, 99999L, LocalDateTime.of(2020, 6, 14, 10, 0)))
                .isInstanceOf(PriceNotFoundException.class);
    }

    @Test
    void shouldThrowPriceNotFoundException_whenBrandDoesNotExist() {
        assertThatThrownBy(() ->
                priceService.findApplicablePrice(99L, 35455L, LocalDateTime.of(2020, 6, 14, 10, 0)))
                .isInstanceOf(PriceNotFoundException.class);
    }

    @Test
    void shouldThrowPriceNotFoundException_whenDateIsOutOfRange() {
        assertThatThrownBy(() ->
                priceService.findApplicablePrice(1L, 35455L, LocalDateTime.of(2019, 1, 1, 0, 0)))
                .isInstanceOf(PriceNotFoundException.class);
    }
}
