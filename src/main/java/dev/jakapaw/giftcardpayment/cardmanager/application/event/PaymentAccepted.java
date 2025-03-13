package dev.jakapaw.giftcardpayment.cardmanager.application.event;

import com.fasterxml.jackson.databind.JsonNode;

public record PaymentAccepted(
        Long cardId,
        JsonNode paymentInfo
) { }
