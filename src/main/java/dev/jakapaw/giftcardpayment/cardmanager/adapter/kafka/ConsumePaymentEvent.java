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
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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
    public void listenPaymentEvent(String payload) {
        try {
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
    public void listenVerifyEvent(String payload) {
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
        } catch (IllegalArgumentException e) {
            VerifyEvent returnEvent = new VerifyEvent(
                    event.invoiceId(),
                    event.cardId(),
                    event.totalBilled(),
                    event.lastBalance(),
                    false,
                    e.getMessage()
            );
            produceVerifyEvent.publish(returnEvent);
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
