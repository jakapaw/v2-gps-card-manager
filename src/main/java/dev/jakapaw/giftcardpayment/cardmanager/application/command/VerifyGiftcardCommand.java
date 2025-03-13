package dev.jakapaw.giftcardpayment.cardmanager.application.command;

public record VerifyGiftcardCommand(
        String invoiceId,
        Long cardId,
        Long totalBilled,
        Long lastBalance,
        Boolean isVerified
) {
}
