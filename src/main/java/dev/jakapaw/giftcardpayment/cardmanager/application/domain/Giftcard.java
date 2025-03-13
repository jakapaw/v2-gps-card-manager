package dev.jakapaw.giftcardpayment.cardmanager.application.domain;

public record Giftcard(
        Long cardId,
        Long balance
) {
    public Giftcard verifyCardId(Long source) {
        if (cardId != source)
            throw new IllegalArgumentException("Card Id is not similar");
        return this;
    }

    public Giftcard verifySufficeBalance(Long source) {
        if (balance < source)
            throw new IllegalArgumentException("Insufficient balance");
        return this;
    }
}
