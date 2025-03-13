package dev.jakapaw.giftcardpayment.cardmanager.adapter.sql;

import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.entity.GiftcardEntity;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.entity.SeriesEntity;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.repository.GiftcardRepository;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.repository.SeriesRepository;
import dev.jakapaw.giftcardpayment.cardmanager.application.port.in.CreateReport;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Reporting implements CreateReport {

    GiftcardRepository giftcardRepository;
    SeriesRepository seriesRepository;
    GiftcardEventSourcing giftcardEventSourcing;

    public Reporting(GiftcardRepository giftcardRepository, GiftcardEventSourcing giftcardEventSourcing) {
        this.giftcardRepository = giftcardRepository;
        this.giftcardEventSourcing = giftcardEventSourcing;
    }

    @Override
    public double meanSpendingValue(String seriesId) {
        SeriesEntity series = seriesRepository.findById(seriesId).orElseThrow();
        List<GiftcardEntity> giftcards = series.getGiftcards();

        double sum = 0;
        for (var el : giftcards) {

        }

        return 0;
    }
}