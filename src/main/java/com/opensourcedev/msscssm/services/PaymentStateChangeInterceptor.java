package com.opensourcedev.msscssm.services;

import com.opensourcedev.msscssm.domain.Payment;
import com.opensourcedev.msscssm.domain.PaymentEvent;
import com.opensourcedev.msscssm.domain.PaymentState;
import com.opensourcedev.msscssm.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {

    private final PaymentRepository paymentRepository;


    @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state, Message<PaymentEvent> message,
                               Transition<PaymentState, PaymentEvent> transition, StateMachine<PaymentState, PaymentEvent> stateMachine) {

        Optional.ofNullable(message).ifPresent(msg ->{
            Optional.ofNullable(Long.class.cast(msg.getHeaders().getOrDefault(PaymentServiceImpl.PAYMENT_ID_HEADER, -1L)))
                    .ifPresent(paymetId ->{
                        Payment payment = paymentRepository.getOne(paymetId);
                        payment.setPaymentState(state.getId());
                        paymentRepository.save(payment);
                    });

        });
    }
}
