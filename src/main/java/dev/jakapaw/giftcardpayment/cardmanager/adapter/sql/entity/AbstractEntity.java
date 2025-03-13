package dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.entity;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Persistable;

@EqualsAndHashCode
public abstract class AbstractEntity<ID> implements Persistable<ID> {

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostLoad
    @PrePersist
    void markNotNew() {
        this.isNew = false;
    }
}
