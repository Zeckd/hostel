package com.work.hostel.services.imps;

import com.work.hostel.mappers.CollateralMapper;
import com.work.hostel.models.Collateral;
import com.work.hostel.models.Resident;
import com.work.hostel.models.dtos.CollateralCreateDto;
import com.work.hostel.models.dtos.CollateralDto;
import com.work.hostel.repositories.CollateralRepo;
import com.work.hostel.repositories.ResidentRepo;
import com.work.hostel.services.CollateralService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class CollateralServiceImpl implements CollateralService {
    private final ResidentRepo residentRepo;
    private final CollateralRepo collateralRepo;
    private final CollateralMapper collateralMapper = CollateralMapper.INSTANCE;

    public CollateralServiceImpl(ResidentRepo residentRepo, CollateralRepo collateralRepo) {
        this.residentRepo = residentRepo;
        this.collateralRepo = collateralRepo;
    }

    @Override
    @Transactional
    public CollateralDto create(CollateralCreateDto collateralCreateDto) {
        Resident resident = residentRepo.findById(collateralCreateDto.residentId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Collateral collateral = Collateral.builder()
                .description(collateralCreateDto.description())
                .returned(false)
                .createdAt(LocalDateTime.now())
                .resident(resident)
                .build();
        resident.setCollateral(collateral);
        collateralRepo.save(collateral);
        return collateralMapper.toCollateralDto(collateral);
    }

    @Override
    public CollateralDto update(Long id, boolean returned) {
        Resident resident = residentRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (resident.getCollateral() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "У жителя нет залога");
        }
        Collateral collateral = collateralRepo.findById(resident.getCollateral().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        collateral.setReturned(returned);
        collateralRepo.save(collateral);
        return collateralMapper.toCollateralDto(collateral);
    }
    // CollateralServiceImpl.java

    @Override
    @Transactional
    public void delete(Long residentId) {
        Resident resident = residentRepo.findById(residentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Житель не найден"));

        Collateral collateral = resident.getCollateral();
        if (collateral != null) {
            resident.setCollateral(null);
            residentRepo.save(resident);

            collateralRepo.delete(collateral);
        }
    }
}
