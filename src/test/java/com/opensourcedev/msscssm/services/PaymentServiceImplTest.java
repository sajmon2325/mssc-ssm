package com.opensourcedev.msscssm.services;

import com.opensourcedev.msscssm.domain.Payment;
import com.opensourcedev.msscssm.domain.PaymentEvent;
import com.opensourcedev.msscssm.domain.PaymentState;
import com.opensourcedev.msscssm.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
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

    @Transactional
    @RepeatedTest(10)
    @Test
    void testAuth(){
        Payment payment = paymentService.newPayment(this.payment);

        StateMachine<PaymentState, PaymentEvent> preAuthSM = paymentService.preAuth(payment.getId());

        if (preAuthSM.getState().getId() == PaymentState.PRE_AUTH){
            System.out.println("Payment is pre-authorized ");
            StateMachine<PaymentState, PaymentEvent> authSM = paymentService.authorizePayment(payment.getId());

            System.out.println("Result of Auth: " + authSM.getState().getId());
        }else {
            System.out.println("Payment failed pre auth...");
        }
        
    }


}