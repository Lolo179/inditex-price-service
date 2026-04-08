package com.inditex.prices.service;

import com.inditex.prices.exception.ErrorMessages;
import com.inditex.prices.exception.PriceNotFoundException;
import com.inditex.prices.repository.PriceRepository;
import com.inditex.prices.service.domain.Price;
import com.inditex.prices.service.mapper.PriceEntityMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService {

    private static final Logger log = LoggerFactory.getLogger(PriceServiceImpl.class);

    private final PriceRepository priceRepository;
    private final PriceEntityMapper priceEntityMapper;

    @Override
    public Price findApplicablePrice(Long brandId, Long productId, LocalDateTime applicationDate) {
        log.debug("Querying applicable price - brandId={}, productId={}, applicationDate={}", brandId, productId, applicationDate);
        return priceRepository.findApplicablePrice(brandId, productId, applicationDate)
                .map(entity -> {
                    Price price = priceEntityMapper.toDomain(entity);
                    log.debug("Price found - priceList={}, price={}", price.priceList(), price.price());
                    return price;
                })
                .orElseThrow(() -> {
                    log.warn("No applicable price found - brandId={}, productId={}, applicationDate={}", brandId, productId, applicationDate);
                    return new PriceNotFoundException(
                            String.format(ErrorMessages.PRICE_NOT_FOUND, brandId, productId, applicationDate)
                    );
                });
    }
}
