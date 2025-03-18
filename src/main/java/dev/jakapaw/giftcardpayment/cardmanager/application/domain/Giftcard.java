package dev.jakapaw.giftcardpayment.cardmanager.application.domain;

public record Giftcard(
        Long cardId,
        Long balance
) {
    public Giftcard(Long cardId, Long balance) {
        if (cardId == null)
            throw new IllegalArgumentException("cardId cannot be null");
        this.cardId = cardId;
        this.balance = balance;
    }

    public Giftcard verifyCardId(Long source) {
        if (!cardId.equals(source))
            throw new IllegalArgumentException("Card Id is not similar");
        return this;
    }

    public Giftcard verifySufficeBalance(Long source) {
        if (balance == null || balance.compareTo(source) < 0)
            throw new IllegalArgumentException("Insufficient balance");
        return this;
    }
}
