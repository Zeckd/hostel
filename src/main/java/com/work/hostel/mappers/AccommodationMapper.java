package com.work.hostel.mappers;

import com.work.hostel.models.Accommodation;
import com.work.hostel.models.Resident;
import com.work.hostel.models.dtos.AccommodationDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AccommodationMapper {
    AccommodationMapper INSTANCE = Mappers.getMapper(AccommodationMapper.class);

    AccommodationDto toAccommodationDto(Accommodation accommodation);
    List<AccommodationDto> toAccommodationDtoList(List<Accommodation> accommodations);


    default List<Long> mapAccommodation(List<Resident> residents) {
        if (residents == null) return List.of();
        return residents.stream()
                .map(Resident::getId)
                .toList();
    }
}
