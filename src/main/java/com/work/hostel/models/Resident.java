package com.work.hostel.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "residents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resident {
    @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String fullName;
    String phoneNumber;
    LocalDate arrivalDate;
    boolean active;
    @ManyToOne
            @JoinColumn(name = "accommodation_id")
    Accommodation accommodation;
    @OneToMany(mappedBy = "resident",cascade = CascadeType.ALL)
            @Builder.Default
    List<Payment> payments = new ArrayList<>();
    @OneToOne(mappedBy = "resident", cascade = CascadeType.ALL)
    Collateral collateral;



    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setResident(this);
    }
    public void addCollateral(Collateral collateral) {
        this.collateral = collateral;
        collateral.setResident(this);
    }

}
