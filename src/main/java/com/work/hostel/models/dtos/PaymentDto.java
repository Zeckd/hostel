package com.work.hostel.models.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

public record PaymentDto(
        Long id,
        Long residentId,
        Long accommodationId,
        YearMonth month,
        int amount,
        boolean paid,
        LocalDateTime paidAt

) {
}
