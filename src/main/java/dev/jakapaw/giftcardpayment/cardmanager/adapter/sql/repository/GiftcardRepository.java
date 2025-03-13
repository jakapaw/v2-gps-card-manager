package dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.repository;

import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.entity.GiftcardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiftcardRepository extends JpaRepository<GiftcardEntity, String> {
}
