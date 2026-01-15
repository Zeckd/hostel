package com.work.hostel.mappers;

import com.work.hostel.models.Collateral;
import com.work.hostel.models.dtos.CollateralDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CollateralMapper {
    CollateralMapper INSTANCE = Mappers.getMapper(CollateralMapper.class);
    @Mapping(source = "resident.id" , target = "residentId")
    CollateralDto toCollateralDto(Collateral collateral);
}
