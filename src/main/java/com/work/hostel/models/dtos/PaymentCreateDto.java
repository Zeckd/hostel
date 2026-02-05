package com.work.hostel.models.dtos;

import com.work.hostel.enums.PaymentType;

import java.math.BigDecimal;

public record PaymentCreateDto(
        Long residentId,
        int amount,
        PaymentType paymentType

) {
}
