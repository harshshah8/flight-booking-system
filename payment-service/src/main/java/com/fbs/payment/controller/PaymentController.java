package com.fbs.payment.controller;

import com.fbs.payment.dto.PaymentRequest;
import com.fbs.payment.dto.PaymentResponse;
import com.fbs.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Payment service REST controller
 */
@RestController
@RequestMapping("/v1/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    /**
     * Process payment for booking
     */
    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        logger.info("Payment processing request received for booking: {}", request.getBookingId());
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.ok(response);
    }

}