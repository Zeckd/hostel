package com.work.hostel.models.dtos;

import com.work.hostel.enums.Type;

import java.util.List;

public record AccommodationDto(
        Long id,
        Type type,
        String name,
        Integer perPersonPrice,
        Integer maxResidents,
        boolean active,
        List<Long> residents
) {
}
