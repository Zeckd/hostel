package com.work.hostel.services.imps;

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
                .month(YearMonth.now())
                .paidAt(LocalDateTime.now())
                .accommodation(accommodation)
                .build();
        if(resident.getAccommodation().getPerPersonPrice() != 0){
            int alreadyPaid = paymentRepo.sumPaidForMonth(resident.getId(), YearMonth.now());

            int price = resident.getAccommodation().getPerPersonPrice();

            int totalAfterPayment = alreadyPaid + payment.getAmount();
            payment.setPaid(totalAfterPayment >= price);
        }

        resident.addPayment(payment);
        paymentRepo.save(payment);
        return paymentMapper.toPaymentDto(payment);
    }

}
