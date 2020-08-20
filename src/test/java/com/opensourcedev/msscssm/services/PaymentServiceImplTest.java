package com.opensourcedev.msscssm.services;

import com.opensourcedev.msscssm.domain.Payment;
import com.opensourcedev.msscssm.domain.PaymentEvent;
import com.opensourcedev.msscssm.domain.PaymentState;
import com.opensourcedev.msscssm.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment().builder().amount(new BigDecimal("12.99")).build();
    }

    @Transactional
    @Test
    void preAuth() {
        Payment savedPaymend = paymentService.newPayment(payment);

        StateMachine<PaymentState, PaymentEvent> sm = paymentService.preAuth(savedPaymend.getId());

        Payment preAuthedPayment = paymentRepository.getOne(savedPaymend.getId());
        
        System.out.println(preAuthedPayment);
    }
}