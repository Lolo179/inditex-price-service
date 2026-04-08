package com.inditex.prices.controller;

import com.inditex.prices.controller.api.PricesApi;
import com.inditex.prices.controller.dto.PriceResponse;
import com.inditex.prices.controller.mapper.PriceDtoMapper;
import com.inditex.prices.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class PriceController implements PricesApi {

    private final PriceService priceService;
    private final PriceDtoMapper priceDtoMapper;

    @Override
    public ResponseEntity<PriceResponse> getApplicablePrice(
            LocalDateTime applicationDate,
            Long productId,
            Long brandId) {
        return ResponseEntity.ok(
                priceDtoMapper.toDto(
                        priceService.findApplicablePrice(brandId, productId, applicationDate)
                )
        );
    }
}
