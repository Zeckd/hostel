package com.work.hostel.models.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record ResidentCreateDto(
        String fullName,
        String phoneNumber,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate arrivalDate,
        Long accommodationId,
        Integer personCount
) {
}
