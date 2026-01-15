package com.work.hostel.models.dtos;

import java.math.BigDecimal;

public record DepositCreateDto (
        Long residentId,
        int amount
){
}
