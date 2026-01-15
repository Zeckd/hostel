package com.work.hostel.services;

import com.work.hostel.models.dtos.AccommodationCreateApartDto;
import com.work.hostel.models.dtos.AccommodationDto;
import com.work.hostel.models.dtos.AccommodationUpdateDto;

import java.util.List;

public interface AccommodationService {
    AccommodationDto createApartment(AccommodationCreateApartDto accommodationCreateApartDto);

    AccommodationDto update(Long id, AccommodationUpdateDto accommodationUpdateDto);

    List<AccommodationDto> getAll();
}
