package com.work.hostel.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "deposits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deposit {
    @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @OneToOne
    Resident resident;
    int amount;
    LocalDateTime paidAt;
}
