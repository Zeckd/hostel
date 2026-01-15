package com.work.hostel.mappers;

import com.work.hostel.models.Accommodation;
import com.work.hostel.models.Payment;
import com.work.hostel.models.Resident;
import com.work.hostel.models.dtos.AccommodationDto;
import com.work.hostel.models.dtos.ResidentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PaymentMapper.class, CollateralMapper.class})
public interface ResidentMapper {
    ResidentMapper INSTANCE = Mappers.getMapper(ResidentMapper.class);

    @Mapping(source = "accommodation.id", target = "accommodationId")
    @Mapping(source = "collateral", target = "collateral")
    ResidentDto toResidentDto(Resident resident);
    List<ResidentDto> toResidentDtoList(List<Resident> residents);


    default List<Long> mapPayments(List<Payment> payments) {
        if (payments == null) return List.of();
        return payments.stream()
                .map(Payment::getId)
                .toList();
    }
}
