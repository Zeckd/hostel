package com.work.hostel.services.imps;

import com.work.hostel.enums.PaymentType;
import com.work.hostel.mappers.PaymentMapper;
import com.work.hostel.models.Accommodation;
import com.work.hostel.models.Payment;
import com.work.hostel.models.Resident;
import com.work.hostel.models.dtos.PaymentCreateDto;
import com.work.hostel.models.dtos.PaymentDto;
import com.work.hostel.repositories.PaymentRepo;
import com.work.hostel.repositories.ResidentRepo;
import com.work.hostel.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final ResidentRepo residentRepo;
    private final PaymentMapper paymentMapper = PaymentMapper.INSTANCE;
    private final PaymentRepo paymentRepo;


    @Override
    public PaymentDto create(PaymentCreateDto paymentCreateDto) {
        Resident resident = residentRepo.findById(paymentCreateDto.residentId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Accommodation accommodation = resident.getAccommodation();

        Payment payment = Payment.builder()
                .amount(paymentCreateDto.amount())
                .paymentType(paymentCreateDto.paymentType())
                .month(YearMonth.now())
                .paidAt(LocalDateTime.now())
                .accommodation(accommodation)
                .build();

        YearMonth currentMonth = YearMonth.now();
        
        // Проверяем, полностью ли оплачено в зависимости от типа оплаты
        if (paymentCreateDto.paymentType() == PaymentType.PER_PERSON) {
            // Оплата за человека - проверяем только оплату конкретного жителя
            if (accommodation.getPerPersonPrice() != null && accommodation.getPerPersonPrice() > 0) {
                int alreadyPaid = paymentRepo.sumPaidForMonthByType(resident.getId(), currentMonth, PaymentType.PER_PERSON);
                // Также учитываем старые платежи без paymentType для этого жителя
                int oldPayments = paymentRepo.sumPaidForMonth(resident.getId(), currentMonth) - alreadyPaid;
                int price = accommodation.getPerPersonPrice();
                int totalAfterPayment = alreadyPaid + oldPayments + payment.getAmount();
                payment.setPaid(totalAfterPayment >= price);
            }
        } else if (paymentCreateDto.paymentType() == PaymentType.FULL_ROOM) {
            // Оплата за всю комнату - проверяем общую сумму всех оплат всех жителей комнаты
            if (accommodation.getFullRentPrice() != null && accommodation.getFullRentPrice() > 0) {
                // Сумма всех оплат за комнату (FULL_ROOM) всех жителей этой комнаты
                int fullRoomPaid = paymentRepo.sumFullRoomPaidForMonthByAccommodation(accommodation.getId(), currentMonth, PaymentType.FULL_ROOM);
                // Сумма всех оплат за человека (PER_PERSON) всех жителей этой комнаты (включая старые без paymentType)
                int perPersonPaid = paymentRepo.sumPerPersonPaidForMonthByAccommodation(accommodation.getId(), currentMonth, PaymentType.PER_PERSON);
                // Общая сумма оплат за комнату (включая текущий платеж)
                int totalRoomPaid = fullRoomPaid + perPersonPaid + payment.getAmount();
                int price = accommodation.getFullRentPrice();
                payment.setPaid(totalRoomPaid >= price);
            }
        }

        resident.addPayment(payment);
        paymentRepo.save(payment);
        return paymentMapper.toPaymentDto(payment);
    }

    @Override
    public int migrateOldPayments() {
        // Устанавливаем paymentType = PER_PERSON для всех платежей, где paymentType = null
        return paymentRepo.updateNullPaymentTypes(PaymentType.PER_PERSON);
    }

}
