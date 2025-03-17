package dev.jakapaw.giftcardpayment.cardmanager.adapter.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.kafka.model.VerifyEvent;
import dev.jakapaw.giftcardpayment.cardmanager.application.command.VerifyGiftcardCommand;
import dev.jakapaw.giftcardpayment.cardmanager.application.event.PaymentAccepted;
import dev.jakapaw.giftcardpayment.cardmanager.application.event.PaymentDeclined;
import dev.jakapaw.giftcardpayment.cardmanager.application.port.in.ListenPaymentEvents;
import dev.jakapaw.giftcardpayment.cardmanager.application.port.in.ManageGiftcard;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapGetter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.NoSuchElementException;

@Component
public class ConsumePaymentEvent {

    ListenPaymentEvents paymentEventHandler;
    ManageGiftcard manageGiftcardDefault;
    ObjectMapper om;
    ProduceVerifyEvent produceVerifyEvent;

    public ConsumePaymentEvent(ListenPaymentEvents paymentEventHandler,
                               ManageGiftcard manageGiftcardDefault,
                               ObjectMapper om,
                               ProduceVerifyEvent produceVerifyEvent) {
        this.paymentEventHandler = paymentEventHandler;
        this.manageGiftcardDefault = manageGiftcardDefault;
        this.om = om;
        this.produceVerifyEvent = produceVerifyEvent;
    }

    @KafkaListener(topics = "giftcard.payment", groupId = "payment")
    public void listenPaymentEvent(String payload, @Headers Map<String, byte[]> headers) {
        ContextPropagators contextPropagators = ContextPropagators.create(W3CTraceContextPropagator.getInstance());
        KafkaHeaderGetter kafkaHeaderGetter = new KafkaHeaderGetter();

        Context extractedContext = contextPropagators.getTextMapPropagator()
                .extract(Context.current(), headers, kafkaHeaderGetter);

        try (Scope scope = extractedContext.makeCurrent()) {
            JsonNode message = om.readTree(payload);
            JsonNode eventData = om.readTree(message.get("eventData").asText());
            String eventName = message.get("eventName").asText();
            if (eventName.equals("PAYMENT_ACCEPTED")) {
                PaymentAccepted paymentAccepted = new PaymentAccepted(Long.valueOf(message.get("cardId").asText()), eventData);
                paymentEventHandler.on(paymentAccepted);
            } else if (eventName.equals("PAYMENT_DECLINED")) {
                PaymentDeclined paymentDeclined = new PaymentDeclined(Long.valueOf(message.get("cardId").asText()), eventData);
                paymentEventHandler.on(paymentDeclined);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "giftcard.verify", groupId = "verifyIn")
    public void listenVerifyEvent(String payload, @Headers Map<String, byte[]> headers) {
        ContextPropagators contextPropagators = ContextPropagators.create(W3CTraceContextPropagator.getInstance());
        KafkaHeaderGetter kafkaHeaderGetter = new KafkaHeaderGetter();

        Context extractedContext = contextPropagators.getTextMapPropagator()
                .extract(Context.current(), headers, kafkaHeaderGetter);

        try (Scope scope = extractedContext.makeCurrent()) {
            VerifyEvent event;
            try{
                event = om.readValue(payload, VerifyEvent.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            VerifyGiftcardCommand command = new VerifyGiftcardCommand(
                    event.invoiceId(),
                    event.cardId(),
                    event.totalBilled(),
                    event.lastBalance(),
                    event.isVerified()
            );

            try {
                manageGiftcardDefault.verifyGiftcard(command);
            } catch (IllegalArgumentException | NoSuchElementException e) {
                VerifyEvent returnEvent = new VerifyEvent(
                        event.invoiceId(),
                        event.cardId(),
                        event.totalBilled(),
                        event.lastBalance(),
                        false,
                        e.getMessage()
                );
                produceVerifyEvent.publish(returnEvent);
                return;
            }

            VerifyEvent returnEvent = new VerifyEvent(
                    event.invoiceId(),
                    event.cardId(),
                    event.totalBilled(),
                    event.lastBalance(),
                    true,
                    null
            );
            produceVerifyEvent.publish(returnEvent);
        }
    }

    private static class KafkaHeaderGetter implements TextMapGetter<Map<String, byte[]>> {

        @Override
        public Iterable<String> keys(Map<String, byte[]> carrier) {
            return carrier.keySet();
        }

        @Override
        public String get(Map<String, byte[]> carrier, String key) {
            if (carrier == null) {
                throw new IllegalArgumentException("Carrier must not be null");
            }
            if (carrier.containsKey(key))
                try {
                    return new String(carrier.get(key), StandardCharsets.UTF_8);
                } catch (Exception e) {
                    return null;
                }
            else
                return null;
        }
    }
}
