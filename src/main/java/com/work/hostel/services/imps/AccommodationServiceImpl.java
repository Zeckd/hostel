package com.work.hostel.services.imps;

import com.work.hostel.mappers.AccommodationMapper;
import com.work.hostel.models.Accommodation;
import com.work.hostel.models.dtos.AccommodationCreateApartDto;
import com.work.hostel.models.dtos.AccommodationDto;
import com.work.hostel.models.dtos.AccommodationUpdateDto;
import com.work.hostel.repositories.AccommodationRepo;
import com.work.hostel.services.AccommodationService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepo accommodationRepo;
    private final AccommodationMapper accommodationMapper = AccommodationMapper.INSTANCE;

    public AccommodationServiceImpl(AccommodationRepo accommodationRepo) {
        this.accommodationRepo = accommodationRepo;
    }

    @Override
    public AccommodationDto createApartment(AccommodationCreateApartDto accommodationCreateApartDto) {
        Accommodation accommodation =Accommodation.builder().name(accommodationCreateApartDto.name())
                .maxResidents(accommodationCreateApartDto.maxResidents())
                .fullRentPrice(accommodationCreateApartDto.fullRentPrice())
                .perPersonPrice(accommodationCreateApartDto.perPersonPrice())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .type(accommodationCreateApartDto.type())
                .build();
        accommodationRepo.save(accommodation);
        return accommodationMapper.toAccommodationDto(accommodation);
    }
    @Override
    public AccommodationDto update(Long id, AccommodationUpdateDto accommodationUpdateDto) {
        Accommodation accommodation = accommodationRepo.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(accommodationUpdateDto.name() != null){
            accommodation.setName(accommodationUpdateDto.name());
        }
        if (accommodationUpdateDto.maxResidents() != null){
            accommodation.setMaxResidents(accommodationUpdateDto.maxResidents());
        }
        if(accommodationUpdateDto.fullRentPrice() != null){
            accommodation.setFullRentPrice(accommodationUpdateDto.fullRentPrice());
        }
        if (accommodationUpdateDto.perPersonPrice() != null){
            accommodation.setPerPersonPrice(accommodationUpdateDto.perPersonPrice());
        }
        accommodationRepo.save(accommodation);
        return accommodationMapper.toAccommodationDto(accommodation);
    }

    @Override
    public List<AccommodationDto> getAll() {
        List<Accommodation> accommodation = accommodationRepo.findAll();
        return accommodationMapper.toAccommodationDtoList(accommodation);
    }

}
