package com.work.hostel.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.work.hostel.enums.Type;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Table(name = "accommodations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    @Enumerated(EnumType.STRING)
    Type type;
    Integer maxResidents;
    Integer fullRentPrice;
    Integer perPersonPrice;
    boolean isFullRent;
    boolean active;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL)
            @Builder.Default
    List<Resident> residents = new ArrayList<>();
    public void addResident(Resident resident) {
        residents.add(resident);
        resident.setAccommodation(this);
    }



}
