package dev.jakapaw.giftcardpayment.cardmanager.application.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public record Series(
        String seriesId,
        List<Giftcard> giftcards,
        Integer totalCards,
        Long totalValue,
        LocalDateTime createdAt
) {

    public Series(String issuerId, int totalCards, long totalValue, int issuerUnique) {
        this(
                generateSeriesId(issuerId),
                generateGiftcards(totalCards, totalValue, issuerUnique),
                totalCards,
                totalValue,
                LocalDateTime.now()
        );
    }

    private static String generateSeriesId(String issuerId) {
        Random random = new Random();
        return issuerId + random.nextInt(10000, 100000);
    }

    private static List<Giftcard> generateGiftcards(int totalCards, long totalValue, int issuerUnique) {
        List<Giftcard> result = new ArrayList<>(totalCards);
        long balance = totalValue / totalCards;
        for (int i = 1; i <= totalCards; i++) {
            result.add(
                    new Giftcard(generateCardId(issuerUnique), balance));
        }
        return  result;
    }

    private static Long generateCardId(long uniqueNumber) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            long num = 0;
            if (i == 0) {
                num = uniqueNumber;
                num %= 10000;
                sb.append(num);
            } else {
                num = random.nextLong(1000, 10000);
                sb.append(num);
            }
        }
        return Long.valueOf(sb.toString());
    }
}
