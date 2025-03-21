package dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.repository;

import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.event.GiftcardEvent;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.event.GiftcardEventId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;

import java.util.Map;

public interface GiftcardEventRepository extends JpaRepository<GiftcardEvent, GiftcardEventId> {

    @NativeQuery(value = "SELECT current_balance, last_version FROM rebuild_state(?1, ?2)")
    Map<String, Object> callRebuildState(Long cardId, Integer version);

    @NativeQuery(value = "SELECT version FROM giftcard_event WHERE card_id=?1 ORDER BY version DESC LIMIT 1")
    Integer getLastVersion(Long cardId);
}
