package com.work.hostel.services.imps;

import com.work.hostel.mappers.ResidentMapper;
import com.work.hostel.models.Accommodation;
import com.work.hostel.models.Resident;
import com.work.hostel.models.dtos.ResidentCreateDto;
import com.work.hostel.models.dtos.ResidentDto;
import com.work.hostel.models.dtos.ResidentUpdateDto;
import com.work.hostel.repositories.AccommodationRepo;
import com.work.hostel.repositories.ResidentRepo;
import com.work.hostel.services.ResidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResidentServiceImpl implements ResidentService {
    private final ResidentRepo residentRepo;
    private final AccommodationRepo accommodationRepo;
    private final ResidentMapper residentMapper;

    @Override
    public ResidentDto create(ResidentCreateDto residentCreateDto) {
        Accommodation accommodation = accommodationRepo.findById(residentCreateDto.accommodationId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        int personCount = residentCreateDto.personCount() != null && residentCreateDto.personCount() > 0 ? residentCreateDto.personCount() : 1;
        
        // Проверяем свободные места с учетом количества человек
        int totalPersonsInRoom = accommodation.getResidents().stream()
                .mapToInt(r -> r.getPersonCount() != null ? r.getPersonCount() : 1)
                .sum();
        if (totalPersonsInRoom + personCount > accommodation.getMaxResidents()) {
            throw new IllegalStateException("No free places");
        }
        
        Resident resident = Resident.builder()
                .fullName(residentCreateDto.fullName())
                .phoneNumber(residentCreateDto.phoneNumber())
                .arrivalDate(residentCreateDto.arrivalDate())
                .personCount(personCount)
                .active(true)
                .build();
        if(resident.getArrivalDate() == null) {
            resident.setArrivalDate(LocalDate.now());
        }



        accommodation.addResident(resident);
        residentRepo.save(resident);


        return residentMapper.toResidentDto(resident);
    }

    @Override
    public ResidentDto update(Long id, ResidentUpdateDto residentUpdateDto) {
        Resident resident = residentRepo.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(residentUpdateDto.accommodationId() != null) {
            Accommodation accommodation = accommodationRepo.findById(residentUpdateDto.accommodationId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            resident.setAccommodation(accommodation);
        }
        if(residentUpdateDto.fullName() != null) {
            resident.setFullName(residentUpdateDto.fullName());
        }
        if(residentUpdateDto.phoneNumber() != null) {
            resident.setPhoneNumber(residentUpdateDto.phoneNumber());
        }
        if(residentUpdateDto.personCount() != null && residentUpdateDto.personCount() > 0) {
            resident.setPersonCount(residentUpdateDto.personCount());
        }

        residentRepo.save(resident);
        return residentMapper.toResidentDto(resident);
    }

    @Override
    public List<ResidentDto> getAll() {
        List<Resident> resident = residentRepo.findAll();
        return residentMapper.toResidentDtoList(resident);
    }
}
