package dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.repository;

import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.event.GiftcardEventSnapshot;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GiftcardEventSnapshotRepository extends JpaRepository<GiftcardEventSnapshot, Long> {

    @Cacheable(key = "#cardId")
    Optional<GiftcardEventSnapshot> findByCardId(Long cardId);

    @CacheEvict(key = "#event.cardId")
    default <S extends GiftcardEventSnapshot> S saveEvent(S event) {
        return save(event);
    }
}
