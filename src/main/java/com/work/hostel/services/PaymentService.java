package com.work.hostel.services;

import com.work.hostel.models.dtos.PaymentCreateDto;
import com.work.hostel.models.dtos.PaymentDto;

public interface PaymentService {
    PaymentDto create(PaymentCreateDto paymentCreateDto);
}
