package com.work.hostel.controllers;

import com.work.hostel.models.dtos.CollateralCreateDto;
import com.work.hostel.models.dtos.CollateralDto;
import com.work.hostel.repositories.CollateralRepo;
import com.work.hostel.services.CollateralService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/collateral")
@RequiredArgsConstructor
public class CollateralController {
    private final CollateralService collateralService;
    private final CollateralRepo collateralRepo;

    @PostMapping("/create")
    public CollateralDto createCollateral(@RequestBody CollateralCreateDto collateralCreateDto) {
        return collateralService.create(collateralCreateDto);
    }
    @PatchMapping("{id}")
    public CollateralDto updateCollateral(@PathVariable Long id, @RequestParam boolean returned){
        return collateralService.update(id,returned);

    }
    @DeleteMapping("/delete/{id}")
    public void deleteCollateral(@PathVariable Long id) {
        collateralService.delete(id);
    }
}
