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
import java.util.Map;
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
            Map<String, Object> rebuildStateRow = eventRepository
                    .callRebuildState(cardId, snapshot.get().getLastVersion());
            lastVersion = (Integer) rebuildStateRow.get("last_version");
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
        Optional<GiftcardEventSnapshot> snapshotOptional = snapshotRepository.findByCardId(cardId);

        Long calculatedBalance = 0L;

        if (snapshotOptional.isPresent()) {
            GiftcardEventSnapshot snapshot = snapshotOptional.get();
            Long snapBalance = snapshot.getBalance();
            Integer snapVersion = snapshot.getLastVersion();

            Map<String, Object> rebuildStateRow = eventRepository.callRebuildState(cardId, snapVersion);
            Integer lastVersion = (Integer) rebuildStateRow.get("last_version");

            calculatedBalance = rebuildStateRow.get("current_balance") != null
                    ? (Long) rebuildStateRow.get("current_balance")
                    : 0L;
            calculatedBalance += snapBalance;

            isCreateSnapshot(snapshot, lastVersion, calculatedBalance);

        } else {
            // rebuild state from beginning
            Map<String, Object> rebuildStateRow = eventRepository.callRebuildState(cardId, 0);
            Long currentBalance = (Long) rebuildStateRow.get("current_balance");
            Integer lastVersion = (Integer) rebuildStateRow.get("last_version");

            // when rebuild state from beginning, we calculate balance by initialBalance - allSpending
            calculatedBalance = currentBalance;

            if (currentBalance != null && lastVersion != null) {
                // save new giftcard event snapshot
                GiftcardEventSnapshot newSnapshot = new GiftcardEventSnapshot();
                newSnapshot.setBalance(currentBalance);
                newSnapshot.setLastVersion(lastVersion);
                newSnapshot.setCardId(cardId);
                isCreateSnapshot(newSnapshot, lastVersion, currentBalance);
            }
            else
                log.error("No giftcard event found for cardId: {}", cardId);
        }

        return new Giftcard(cardId, calculatedBalance);
    }

    public void isCreateSnapshot(GiftcardEventSnapshot snapshot, Integer newVersion, Long newBalance) {
        if (newVersion % 5 == 0) {
            snapshot.setLastVersion(newVersion);
            snapshot.setBalance(newBalance);
            snapshotRepository.save(snapshot);
        }
    }
}
