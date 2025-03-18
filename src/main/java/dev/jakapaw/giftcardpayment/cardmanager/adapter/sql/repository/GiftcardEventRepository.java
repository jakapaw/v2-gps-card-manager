package dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.repository;

import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.event.GiftcardEvent;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.event.GiftcardEventId;
import dev.jakapaw.giftcardpayment.cardmanager.application.domain.Giftcard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GiftcardEventRepository extends JpaRepository<GiftcardEvent, GiftcardEventId> {

    @NativeQuery(value = "SELECT rebuild_state(?1)")
    Long getCurrentBalance(Long cardId);

    @NativeQuery(value = "SELECT version FROM giftcard_event WHERE card_id=?1 ORDER BY version DESC LIMIT 1")
    Integer getLastVersion(Long cardId);
}
