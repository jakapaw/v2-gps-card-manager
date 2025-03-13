package dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.event;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@IdClass(GiftcardEventId.class)
@AllArgsConstructor
@Getter
public class GiftcardEvent {

    @Id
    @JdbcTypeCode(SqlTypes.BIGINT)
    private Long cardId;
    @Id
    private Integer version;

    private String eventName;

    private Long balanceChange;

    @JdbcTypeCode(SqlTypes.JSON)
    private String eventData;

}
