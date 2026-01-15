package com.work.hostel.models.dtos;

import com.work.hostel.enums.Type;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;

public record AccommodationCreateApartDto(
        String name,
        @Enumerated(EnumType.STRING)
        Type type,
        Integer maxResidents,
        Integer perPersonPrice,
        Integer fullRentPrice


) {
}
