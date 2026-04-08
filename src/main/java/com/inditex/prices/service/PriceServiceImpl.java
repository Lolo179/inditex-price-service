package com.inditex.prices.service;

import com.inditex.prices.exception.PriceNotFoundException;
import com.inditex.prices.repository.PriceRepository;
import com.inditex.prices.service.domain.Price;
import com.inditex.prices.service.mapper.PriceEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;
    private final PriceEntityMapper priceEntityMapper;

    @Override
    public Price findApplicablePrice(Long brandId, Long productId, LocalDateTime applicationDate) {
        return priceRepository.findApplicablePrice(brandId, productId, applicationDate)
                .map(priceEntityMapper::toDomain)
                .orElseThrow(() -> new PriceNotFoundException(
                        String.format("No applicable price found for brandId=%d, productId=%d at %s",
                                brandId, productId, applicationDate)
                ));
    }
}
