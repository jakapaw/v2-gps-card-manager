package dev.jakapaw.giftcardpayment.cardmanager.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.GiftcardEventSourcing;
import dev.jakapaw.giftcardpayment.cardmanager.application.domain.Giftcard;
import dev.jakapaw.giftcardpayment.cardmanager.application.event.PaymentAccepted;
import dev.jakapaw.giftcardpayment.cardmanager.application.event.PaymentDeclined;
import dev.jakapaw.giftcardpayment.cardmanager.application.event.VerificationDone;
import dev.jakapaw.giftcardpayment.cardmanager.application.port.in.ListenPaymentEvents;
import dev.jakapaw.giftcardpayment.cardmanager.application.port.in.ListenVerificationEvent;
import org.springframework.stereotype.Service;

@Service
public class GiftcardEventHandler implements ListenPaymentEvents, ListenVerificationEvent {

    GiftcardEventSourcing giftcardEventSourcing;
    ObjectMapper om;

    public GiftcardEventHandler(GiftcardEventSourcing giftcardEventSourcing, ObjectMapper om) {
        this.giftcardEventSourcing = giftcardEventSourcing;
        this.om = om;
    }

    @Override
    public void on(PaymentAccepted event) {
        Giftcard giftcard = giftcardEventSourcing.rebuildState(event.cardId()).orElseThrow();
        JsonNode seller = event.paymentInfo().path("seller");
        long totalBill = seller.get("totalBill").asLong();
        try {
            giftcardEventSourcing.pushEvent(
                    event.cardId(),
                    om.writeValueAsString(event.paymentInfo()),
                    -1 * totalBill,
                    PaymentAccepted.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void on(PaymentDeclined event) {
        try {
            giftcardEventSourcing.pushEvent(
                    event.cardId(),
                    om.writeValueAsString(event.paymentInfo()),
                    0,
                    PaymentDeclined.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void on(VerificationDone event) {
        try {
            giftcardEventSourcing.pushEvent(
                    event.cardId(),
                    om.writeValueAsString(event.data()),
                    0,
                    VerificationDone.class
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
