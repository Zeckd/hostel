package com.work.hostel.controllers;

import com.work.hostel.models.Accommodation;
import com.work.hostel.models.dtos.AccommodationCreateApartDto;
import com.work.hostel.models.dtos.AccommodationDto;
import com.work.hostel.models.dtos.AccommodationUpdateDto;
import com.work.hostel.repositories.AccommodationRepo;
import com.work.hostel.services.AccommodationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accommodation")
public class AccommodationController {
    private final AccommodationService accommodationService;
    private final AccommodationRepo accommodationRepo;

    public AccommodationController(AccommodationService accommodationService, AccommodationRepo accommodationRepo) {
        this.accommodationService = accommodationService;
        this.accommodationRepo = accommodationRepo;
    }

    @PostMapping("/create")
    public AccommodationDto createApartment (@RequestBody AccommodationCreateApartDto accommodationCreateApartDto) {
        return accommodationService.createApartment(accommodationCreateApartDto);
    }
    @PatchMapping("/{id}")
    public AccommodationDto update(@PathVariable Long id, @RequestBody AccommodationUpdateDto accommodationUpdateDto) {
        return accommodationService.update(id,accommodationUpdateDto);

    }
    @GetMapping("/get/all")
    public List<AccommodationDto> getAll() {
        return accommodationService.getAll();
    }
    @DeleteMapping("/delete/{id}")
    public void deleteRoom(@PathVariable Long id) {
        accommodationRepo.deleteById(id);
    }


}
