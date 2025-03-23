package dev.jakapaw.giftcardpayment.cardmanager.config;

import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.GiftcardEventSourcing;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.repository.ExperimentalGiftcardEventSnapshotRepository;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.repository.GiftcardEventRepository;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.repository.GiftcardEventSnapshotRepository;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.repository.SeriesRepository;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExperimentalFeaturesConfig {

    private static final Logger log = LoggerFactory.getLogger(ExperimentalFeaturesConfig.class);

    @Bean
    @ConditionalOnProperty(
            name = "features.giftcard-event-snapshot-cached",
            matchIfMissing = true,
            havingValue = "false"
    )
    GiftcardEventSourcing giftcardEventSourcing(
            EntityManager entityManager,
            SeriesRepository seriesRepository,
            GiftcardEventRepository eventRepository,
            GiftcardEventSnapshotRepository giftcardEventSnapshotRepository
    ) {
        return new GiftcardEventSourcing(entityManager, seriesRepository, eventRepository, giftcardEventSnapshotRepository);
    }

    @Bean(name = "giftcardEventSourcing")
    @ConditionalOnProperty(
            name = "features.giftcard-event-snapshot-cached",
            havingValue = "true"
    )
    GiftcardEventSourcing experimentalGiftcardEventSourcing(
            EntityManager entityManager,
            SeriesRepository seriesRepository,
            GiftcardEventRepository eventRepository,
            ExperimentalGiftcardEventSnapshotRepository experimentalGiftcardEventSnapshotRepository
    ) {
        log.warn("Experimental feature cached giftcard event snapshot is active.");
        return new GiftcardEventSourcing(entityManager, seriesRepository, eventRepository, experimentalGiftcardEventSnapshotRepository);
    }
}
