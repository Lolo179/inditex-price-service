package com.inditex.prices.service.mapper;

import com.inditex.prices.repository.entity.PriceEntity;
import com.inditex.prices.service.domain.Price;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PriceEntityMapper {

    Price toDomain(PriceEntity entity);
}
