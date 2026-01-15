package com.work.hostel.services;

import com.work.hostel.models.dtos.ResidentCreateDto;
import com.work.hostel.models.dtos.ResidentDto;
import com.work.hostel.models.dtos.ResidentUpdateDto;

import java.util.List;

public interface ResidentService {
    ResidentDto create(ResidentCreateDto residentCreateDto);

    ResidentDto update(Long id, ResidentUpdateDto residentUpdateDto);

    List<ResidentDto> getAll();
}
