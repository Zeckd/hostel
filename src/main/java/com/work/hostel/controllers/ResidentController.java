package com.work.hostel.controllers;

import com.work.hostel.models.Resident;
import com.work.hostel.models.dtos.ResidentCreateDto;
import com.work.hostel.models.dtos.ResidentDto;
import com.work.hostel.models.dtos.ResidentUpdateDto;
import com.work.hostel.repositories.ResidentRepo;
import com.work.hostel.services.ResidentService;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resident")
@RequiredArgsConstructor
public class ResidentController {
    private final ResidentService residentService;
    private final ResidentRepo residentRepo;

    @PostMapping("/create")
    public ResidentDto createResident(@RequestBody ResidentCreateDto residentCreateDto) {
        return residentService.create(residentCreateDto);

    }
    @PatchMapping("/{id}")
    public ResidentDto updateResident(@PathVariable Long id, @RequestBody ResidentUpdateDto residentUpdateDto) {
        return residentService.update(id,residentUpdateDto);
    }
    @GetMapping("/getAll")
    public List<ResidentDto> getAll() {
        return residentService.getAll();
    }
    @DeleteMapping("/delete/{id}")
    public void deleteResident(@PathVariable Long id) {
        residentRepo.deleteById(id);
    }
}
