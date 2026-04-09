package com.inditex.prices.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.inditex.prices.service.domain.Currency;

@Entity
@Table(
        name = "PRICES",
        // Index ordered by priority DESC before date ranges so the DB can stop at the first
        // row that satisfies the date filter (early-exit with LIMIT 1), avoiding an in-memory
        // sort. On H2 the DESC hint is advisory; on PostgreSQL/MySQL 8+ it is fully enforced.
        indexes = @Index(
                name = "idx_prices_lookup",
                columnList = "brand_id, product_id, priority DESC, start_date, end_date"
        )
)
@Getter
@Setter
@NoArgsConstructor
public class PriceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "brand_id", nullable = false)
    private Long brandId;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "price_list", nullable = false)
    private Integer priceList;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "curr", nullable = false, length = 3)
    private Currency currency;
}
