package dev.jakapaw.giftcardpayment.cardmanager.application.event;

import com.fasterxml.jackson.databind.JsonNode;

public record PaymentDeclined(
        Long cardId,
        JsonNode paymentInfo
) { }
