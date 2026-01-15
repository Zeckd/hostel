package com.work.hostel.models.dtos;

import java.math.BigDecimal;

public record PaymentCreateDto(
        Long residentId,
        int amount

) {
}
