package dev.jakapaw.giftcardpayment.cardmanager.application.event;

import com.fasterxml.jackson.databind.JsonNode;

public record VerificationDone(
        Long cardId,
        JsonNode data
) {
}
