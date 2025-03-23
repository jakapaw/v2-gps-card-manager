package dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.repository;

import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.event.GiftcardEventSnapshot;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ExperimentalGiftcardEventSnapshotRepository extends GiftcardEventSnapshotRepository {

    @Cacheable(key = "#cardId")
    Optional<GiftcardEventSnapshot> findByCardId(Long cardId);

    @CacheEvict(key = "#event.cardId")
    default <S extends GiftcardEventSnapshot> S saveEvent(S event) {
        return save(event);
    }
}
