package com.work.hostel.models.dtos;

import java.math.BigDecimal;

public record AccommodationUpdateDto(
        String name,
        Integer maxResidents,
        Integer perPersonPrice,
        Integer fullRentPrice
) {
}
