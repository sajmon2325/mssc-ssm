package com.opensourcedev.msscssm.services;

import com.opensourcedev.msscssm.domain.Payment;
import com.opensourcedev.msscssm.domain.PaymentEvent;
import com.opensourcedev.msscssm.domain.PaymentState;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {

    Payment newPayment(Payment payment);

    StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId);
    StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId);
    StateMachine<PaymentState, PaymentEvent> declinePayment(Long paymentId);



}
