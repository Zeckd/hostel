package com.work.hostel.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne()
            @JoinColumn(name = "resident_id")
    Resident resident;
    @ManyToOne
            @JoinColumn(name = "accommodation_id")
    Accommodation accommodation;
    YearMonth month;
    int amount;
    boolean paid;
    LocalDateTime paidAt;
}
