package com.work.hostel.models.dtos;

import com.work.hostel.models.Collateral;
import com.work.hostel.models.Payment;

import java.time.LocalDate;
import java.util.List;

public record ResidentDto(
        Long id,
        String fullName,
        String phoneNumber,
        LocalDate arrivalDate,
        boolean active,
        Long accommodationId,
        List<PaymentDto> payments,
        CollateralDto collateral


) {
}
