package dev.jakapaw.giftcardpayment.cardmanager.adapter.sql;

import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.event.GiftcardEvent;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.event.GiftcardEventSnapshot;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.repository.GiftcardEventRepository;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.repository.GiftcardEventSnapshotRepository;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.repository.SeriesRepository;
import dev.jakapaw.giftcardpayment.cardmanager.application.domain.Giftcard;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
public class GiftcardEventSourcing {

    private static final Logger log = LoggerFactory.getLogger(GiftcardEventSourcing.class);

    EntityManager entityManager;
    SeriesRepository seriesRepository;
    GiftcardEventRepository eventRepository;
    GiftcardEventSnapshotRepository snapshotRepository;

    public GiftcardEventSourcing(EntityManager entityManager, SeriesRepository seriesRepository,
                                 GiftcardEventRepository eventRepository,
                                 GiftcardEventSnapshotRepository snapshotRepository) {
        this.entityManager = entityManager;
        this.seriesRepository = seriesRepository;
        this.eventRepository = eventRepository;
        this.snapshotRepository = snapshotRepository;
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

    @Transactional
    public void pushEvent(long cardId, String eventData, long balanceChange, Class<?> event) {
        Optional<GiftcardEventSnapshot> snapshot = snapshotRepository.findByCardId(cardId);
        Integer lastVersion;

        if (snapshot.isPresent()) {
            lastVersion = snapshot.get().getLastVersion();
        } else {
            lastVersion = eventRepository.getLastVersion(cardId);
        }

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

    @Transactional
    public Giftcard rebuildState(Long cardId) {
        Optional<GiftcardEventSnapshot> snapshot = snapshotRepository.findByCardId(cardId);

        Long snapBalance = 0L;
        Integer snapVersion = 0;
        Long calculatedBalance = 0L;
        if (snapshot.isPresent()) {
            snapBalance = snapshot.get().getBalance();
            snapVersion = snapshot.get().getLastVersion();

            calculatedBalance = (Long) eventRepository.callRebuildState(cardId, snapVersion).get("current_balance");
            calculatedBalance += snapBalance;

            Integer lastVersion = (Integer) eventRepository.callRebuildState(cardId, snapVersion).get("last_version");
            isCreateSnapshot(cardId, calculatedBalance, lastVersion );
        } else {
            Long currentBalance = (Long) eventRepository.callRebuildState(cardId, snapVersion).get("current_balance");
            Integer lastVersion = (Integer) eventRepository.callRebuildState(cardId, snapVersion).get("last_version");
            // when rebuild balance from beginning, we calculate balance from initialBalance - spending
            calculatedBalance = currentBalance;
            if (currentBalance != null && lastVersion != null)
                isCreateSnapshot(cardId, currentBalance, lastVersion);
            else
                log.error("No event found for cardId: {}", cardId);
        }
        return new Giftcard(cardId, calculatedBalance);
    }

    public void isCreateSnapshot(Long cardId, Long balance, Integer version) {
        if (version % 5 == 4) {
            Session session = entityManager.unwrap(Session.class);
            snapshotRepository.findByCardId(cardId)
                    .ifPresentOrElse((el -> {
                        el.setBalance(balance);
                        el.setLastVersion(version);
                        snapshotRepository.save(el);
                    }), () -> {
                        snapshotRepository.save(new GiftcardEventSnapshot(cardId, version, balance));
                    });
        }
    }
}
