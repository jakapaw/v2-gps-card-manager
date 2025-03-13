package dev.jakapaw.giftcardpayment.cardmanager.adapter.sql;

import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.entity.GiftcardEntity;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.entity.SeriesEntity;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.repository.GiftcardRepository;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.repository.SeriesRepository;
import dev.jakapaw.giftcardpayment.cardmanager.application.domain.Giftcard;
import dev.jakapaw.giftcardpayment.cardmanager.application.domain.Series;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GiftcardDAO {

    GiftcardRepository giftcardRepository;
    SeriesRepository seriesRepository;

    public GiftcardDAO(GiftcardRepository giftcardRepository, SeriesRepository seriesRepository) {
        this.giftcardRepository = giftcardRepository;
        this.seriesRepository = seriesRepository;
    }

    public void saveAll(List<Giftcard> giftcards, Series series) {
        List<GiftcardEntity> giftcardEntities = new ArrayList<>(giftcards.size());
        SeriesEntity seriesEntity = seriesRepository.findById(series.seriesId()).orElseGet(
                () -> SeriesEntity.buildFromDomain(series)
        );

        giftcards.forEach(el -> {
            GiftcardEntity entity = GiftcardEntity.buildFromDomain(el, seriesEntity);
            giftcardEntities.add(entity);
        });

        giftcardRepository.saveAllAndFlush(giftcardEntities);
    }
}
