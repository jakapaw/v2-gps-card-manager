package dev.jakapaw.giftcardpayment.cardmanager.adapter.sql;

import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.entity.GiftcardEntity;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.entity.SeriesEntity;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.event.GiftcardEvent;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.event.GiftcardEventRowMapper;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.repository.GiftcardEventRepository;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.repository.SeriesRepository;
import dev.jakapaw.giftcardpayment.cardmanager.application.domain.Giftcard;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
public class GiftcardEventSourcing {

    private static final Logger log = LoggerFactory.getLogger(GiftcardEventSourcing.class);

    JdbcTemplate jdbcTemplate;
    SeriesRepository seriesRepository;
    GiftcardEventRepository eventRepository;

    public GiftcardEventSourcing(JdbcTemplate jdbcTemplate, SeriesRepository seriesRepository, GiftcardEventRepository eventRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.seriesRepository = seriesRepository;
        this.eventRepository = eventRepository;
    }

    public void pushGiftcardCreated(long cardId, String eventData, long balance, Class<?> event) {
        GiftcardEvent newEvent = new GiftcardEvent(
                cardId,
                1,
                event.getSimpleName(),
                balance,
                eventData,
                LocalDateTime.now()
        );
        eventRepository.save(newEvent);
    }

    public void pushEvent(long cardId, String eventData, long balanceChange, Class<?> event) {
        Integer lastVersion = eventRepository.getLastVersion(cardId);

        if (lastVersion == null) {
            log.error("No event found for cardId: {}", cardId);
            return;
        }
        lastVersion += 1;

        GiftcardEvent newEvent = new GiftcardEvent(
                cardId,
                lastVersion,
                event.getSimpleName(),
                balanceChange,
                eventData,
                LocalDateTime.now()
        );
        eventRepository.save(newEvent);
    }

    public Giftcard rebuildState(Long cardId) {
        Long currentBalance = eventRepository.getCurrentBalance(cardId);
        return new Giftcard(cardId, currentBalance);
    }
}
