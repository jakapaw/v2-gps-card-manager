package dev.jakapaw.giftcardpayment.cardmanager.adapter.sql;

import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.entity.GiftcardEntity;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.entity.SeriesEntity;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.event.GiftcardEvent;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.event.GiftcardEventRowMapper;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.repository.SeriesRepository;
import dev.jakapaw.giftcardpayment.cardmanager.application.domain.Giftcard;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
public class GiftcardEventSourcing {

    SeriesRepository seriesRepository;
    JdbcTemplate jdbcTemplate;

    public GiftcardEventSourcing(SeriesRepository seriesRepository, JdbcTemplate jdbcTemplate) {
        this.seriesRepository = seriesRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Giftcard> rebuildState(Long cardId) {
        String sql = String.format("""
                SELECT * FROM giftcard_event WHERE card_id = %s
                ORDER BY version;
                """, cardId);
        List<GiftcardEvent> events = jdbcTemplate.query(sql, new GiftcardEventRowMapper());

        long finalBalance = events.getFirst().getBalanceChange();
        for (var event : events) {
            finalBalance += event.getBalanceChange();
        }

        Giftcard result = new Giftcard(cardId, finalBalance);
        return Optional.of(result);
    }

    public void pushGiftcardCreated(long cardId, String eventData, long balance, Class<?> event) {
        String sql = String.format("""
                        INSERT INTO giftcard_event (card_id, version, event_name, balance_change, event_data, created_at)
                        VALUES (%s, %d, '%s', %d, '%s', '%s');
                        """, cardId, 1, event.getSimpleName(), balance, eventData, LocalDateTime.now());
        jdbcTemplate.update(sql);
    }

    public void pushEvent(long cardId, String eventData, long balanceChange, Class<?> event) {
        String select = String.format("""
                SELECT version FROM giftcard_event WHERE card_id = %s
                ORDER BY version DESC LIMIT 1;
                """, cardId);
        Integer lastVersion = jdbcTemplate.queryForObject(select, Integer.class);
        if (lastVersion == null) {
            throw new NoSuchElementException("No event found for cardId: " + cardId);
        }
        lastVersion += 1;

        String sql = String.format("""
                        INSERT INTO giftcard_event (card_id, version, event_name, balance_change, event_data, created_at)
                        VALUES (%s, %d, '%s', %d, '%s', '%s');
                        """, cardId, lastVersion, event.getSimpleName(), balanceChange, eventData, LocalDateTime.now());
        jdbcTemplate.update(sql);
    }
}
