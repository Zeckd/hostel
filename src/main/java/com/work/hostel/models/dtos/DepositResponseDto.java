package com.work.hostel.models.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DepositResponseDto(
        Long id,
        Long residentId,
        int amount,
        boolean refunded,
        LocalDateTime paidAt
) {
}
