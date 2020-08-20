package com.opensourcedev.msscssm.config;

import com.opensourcedev.msscssm.domain.PaymentEvent;
import com.opensourcedev.msscssm.domain.PaymentState;
import com.opensourcedev.msscssm.services.PaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;
import java.util.Random;

@Slf4j
@Configuration
@EnableStateMachineFactory //enable to have a component to generate state machines
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states.withStates()
                .initial(PaymentState.NEW)  // initial state of state machine
                .states(EnumSet.allOf(PaymentState.class)) //It's getting list of enumerations from payment state and it's loading that up into all the states
                .end(PaymentState.AUTH)     //If  one of these states are hit the we have hit an end
                .end(PaymentState.PRE_AUTH_ERROR)
                .end(PaymentState.AUTH_ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions.withExternal().source(PaymentState.NEW).target(PaymentState.NEW).event(PaymentEvent.PRE_AUTHORIZE)
                .action(preAuthAction())
                .and().withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH).event(PaymentEvent.PRE_AUTH_APPROVED)
                .and().withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH_ERROR).event(PaymentEvent.PRE_AUTH_DECLINED);

        // source - initial state --> if target state is NEW than assign event of PRE_AUTHORIZE
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
        StateMachineListenerAdapter<PaymentState, PaymentEvent> adapter = new StateMachineListenerAdapter<>(){
            @Override
            public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
                log.info(String.format("State Changed (from: %s, to: %s)", from, to));
            }
        };

        config.withConfiguration().listener(adapter);
    }

    public Action<PaymentState, PaymentEvent> preAuthAction(){
        return context ->{
            System.out.println("PreAth() was called ... ");

            if (new Random().nextInt(10) < 8){
                System.out.println("Approved!!");
                context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_APPROVED)
                        .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER,context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER)).build());

            }else {
                System.out.println("Declined, Payment DECLINED!!!");
                context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_DECLINED)
                        .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER,context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER)).build());
            }

        };
    }

}
