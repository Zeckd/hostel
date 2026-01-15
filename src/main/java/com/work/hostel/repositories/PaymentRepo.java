package com.work.hostel.repositories;

import com.work.hostel.models.Payment;
import com.work.hostel.models.dtos.PaymentCreateDto;
import com.work.hostel.models.dtos.PaymentDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {
    @Query(" SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.resident.id = :residentId AND p.month = :month ")
    int sumPaidForMonth(Long residentId, YearMonth month);
}
