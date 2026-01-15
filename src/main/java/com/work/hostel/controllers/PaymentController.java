package com.work.hostel.controllers;

import com.work.hostel.models.dtos.PaymentCreateDto;
import com.work.hostel.models.dtos.PaymentDto;
import com.work.hostel.repositories.PaymentRepo;
import com.work.hostel.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    @PostMapping("/create")
    public PaymentDto createPayment(@RequestBody PaymentCreateDto paymentCreateDto) {
        return paymentService.create(paymentCreateDto);
    }
}
