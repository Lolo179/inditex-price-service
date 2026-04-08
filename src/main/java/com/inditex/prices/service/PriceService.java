package com.inditex.prices.service;

import com.inditex.prices.service.domain.Price;

import java.time.LocalDateTime;

public interface PriceService {

    Price findApplicablePrice(Long brandId, Long productId, LocalDateTime applicationDate);
}
