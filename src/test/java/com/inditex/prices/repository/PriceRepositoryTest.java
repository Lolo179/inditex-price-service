package com.inditex.prices.repository;

import com.inditex.prices.repository.entity.PriceEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JPA integration tests.
 *
 * @DataJpaTest spins up a minimal Spring context with only JPA/Hibernate beans
 * and an isolated in-memory H2 instance (separate from the main app datasource).
 * Hibernate creates the schema from @Entity classes (ddl-auto=create-drop).
 * @Sql loads the sample data before each test method, within the test transaction
 * which is automatically rolled back after each test — keeping tests independent.
 */
@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Sql("/data.sql")
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
