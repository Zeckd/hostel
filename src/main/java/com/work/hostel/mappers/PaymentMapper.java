package com.work.hostel.mappers;

import com.work.hostel.models.Payment;
import com.work.hostel.models.dtos.PaymentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);
    @Mapping(source = "id", target = "id")
    @Mapping(source = "resident.id", target = "residentId")
    @Mapping(source = "resident.accommodation.id", target = "accommodationId")
    PaymentDto toPaymentDto(Payment payment);
}
