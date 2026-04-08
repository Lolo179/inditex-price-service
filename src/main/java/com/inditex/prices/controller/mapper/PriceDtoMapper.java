package com.inditex.prices.controller.mapper;

import com.inditex.prices.controller.dto.PriceResponse;
import com.inditex.prices.service.domain.Price;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PriceDtoMapper {

    PriceResponse toDto(Price price);
}
