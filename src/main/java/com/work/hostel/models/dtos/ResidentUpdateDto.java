package com.work.hostel.models.dtos;

public record ResidentUpdateDto (
        String fullName,
        String phoneNumber,
        Long accommodationId
){
}
