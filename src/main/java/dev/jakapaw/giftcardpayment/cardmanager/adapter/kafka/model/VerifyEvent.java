package dev.jakapaw.giftcardpayment.cardmanager.adapter.kafka.model;

public record VerifyEvent(
        String invoiceId,
        Long cardId,
        Long totalBilled,
        Long lastBalance,
        Boolean isVerified,
        String message
) { }
