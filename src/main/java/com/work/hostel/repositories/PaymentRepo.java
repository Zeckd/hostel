package com.work.hostel.repositories;

import com.work.hostel.enums.PaymentType;
import com.work.hostel.models.Payment;
import com.work.hostel.models.dtos.PaymentCreateDto;
import com.work.hostel.models.dtos.PaymentDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {
    @Query(" SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.resident.id = :residentId AND p.month = :month ")
    int sumPaidForMonth(Long residentId, YearMonth month);

    @Query(" SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.resident.id = :residentId AND p.month = :month AND p.paymentType = :paymentType ")
    int sumPaidForMonthByType(@Param("residentId") Long residentId, @Param("month") YearMonth month, @Param("paymentType") PaymentType paymentType);

    // Сумма всех оплат за комнату (FULL_ROOM) всех жителей комнаты за месяц
    @Query(" SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.accommodation.id = :accommodationId AND p.month = :month AND p.paymentType = :paymentType ")
    int sumFullRoomPaidForMonthByAccommodation(@Param("accommodationId") Long accommodationId, @Param("month") YearMonth month, @Param("paymentType") PaymentType paymentType);

    // Сумма всех оплат за человека (PER_PERSON) всех жителей комнаты за месяц (включая старые платежи без paymentType)
    @Query(" SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.accommodation.id = :accommodationId AND p.month = :month AND (p.paymentType = :paymentType OR p.paymentType IS NULL) ")
    int sumPerPersonPaidForMonthByAccommodation(@Param("accommodationId") Long accommodationId, @Param("month") YearMonth month, @Param("paymentType") PaymentType paymentType);

    @Modifying
    @Transactional
    @Query("UPDATE Payment p SET p.paymentType = :paymentType WHERE p.paymentType IS NULL")
    int updateNullPaymentTypes(@Param("paymentType") PaymentType paymentType);
}
