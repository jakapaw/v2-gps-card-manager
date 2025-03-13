package dev.jakapaw.giftcardpayment.cardmanager.application.command;

public record CreateSeriesCommand(
        String issuerId,
        int totalCards,
        long totalValue,
        int issuerUnique
) {
}
