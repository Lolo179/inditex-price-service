package com.inditex.prices.controller;

import com.inditex.prices.controller.api.PricesApi;
import com.inditex.prices.controller.dto.PriceResponse;
import com.inditex.prices.controller.mapper.PriceDtoMapper;
import com.inditex.prices.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Validated
@RestController
@RequiredArgsConstructor
public class PriceController implements PricesApi {

    private static final Logger log = LoggerFactory.getLogger(PriceController.class);

    private final PriceService priceService;
    private final PriceDtoMapper priceDtoMapper;

    @Override
    public ResponseEntity<PriceResponse> getApplicablePrice(
            LocalDateTime applicationDate,
            Long productId,
            Long brandId) {
        log.info("GET /api/v1/prices - productId={}, brandId={}, applicationDate={}", productId, brandId, applicationDate);
        PriceResponse response = priceDtoMapper.toDto(
                priceService.findApplicablePrice(brandId, productId, applicationDate)
        );
        log.debug("Response: priceList={}, price={}, currency={}", response.getPriceList(), response.getPrice(), response.getCurrency());
        return ResponseEntity.ok(response);
    }
}
