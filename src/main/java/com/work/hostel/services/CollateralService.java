package com.work.hostel.services;

import com.work.hostel.models.dtos.CollateralCreateDto;
import com.work.hostel.models.dtos.CollateralDto;

public interface CollateralService {
    CollateralDto create(CollateralCreateDto collateralCreateDto);


    CollateralDto update(Long id, boolean returned);
}
