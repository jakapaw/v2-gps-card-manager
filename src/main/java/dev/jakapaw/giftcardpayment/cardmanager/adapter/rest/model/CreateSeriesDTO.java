package dev.jakapaw.giftcardpayment.cardmanager.adapter.rest.model;

public record CreateSeriesDTO(
        String issuerId,
        int totalCards,
        long totalValue,
        int issuerUnique
) {
}
