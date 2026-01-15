package com.work.hostel.models.dtos;

import java.time.LocalDateTime;

public record CollateralDto (
        Long id,
        Long residentId,
        String description,
        boolean returned,
        LocalDateTime createdAt
){
}
