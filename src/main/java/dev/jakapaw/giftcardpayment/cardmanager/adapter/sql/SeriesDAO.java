package dev.jakapaw.giftcardpayment.cardmanager.adapter.sql;

import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.entity.SeriesEntity;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.repository.SeriesRepository;
import dev.jakapaw.giftcardpayment.cardmanager.application.domain.Series;
import org.springframework.stereotype.Component;

@Component
public class SeriesDAO {

    SeriesRepository seriesRepository;

    public SeriesDAO(SeriesRepository seriesRepository) {
        this.seriesRepository = seriesRepository;
    }

    public void save(Series series) {
        SeriesEntity entity = SeriesEntity.buildFromDomain(series);
        seriesRepository.save(entity);
    }
}
