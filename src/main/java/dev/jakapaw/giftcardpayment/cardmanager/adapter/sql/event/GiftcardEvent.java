package dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.event;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@IdClass(GiftcardEventId.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GiftcardEvent {

    @Id
    @JdbcTypeCode(SqlTypes.BIGINT)
    private Long cardId;
    @Id
    private Integer version;

    private String eventName;

    @JdbcTypeCode(SqlTypes.BIGINT)
    private Long balanceChange;

    @JdbcTypeCode(SqlTypes.JSON)
    private String eventData;

    private LocalDateTime createdAt;
}
