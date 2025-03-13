package dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.entity;

import dev.jakapaw.giftcardpayment.cardmanager.application.domain.Giftcard;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "giftcard")
public class GiftcardEntity extends AbstractEntity<String> {

    @Id
    private Long cardId;

    @NotNull @PositiveOrZero
    Long balance;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "series_id")
    private SeriesEntity seriesId;

    @Override
    public String getId() {
        return "";
    }

    public static GiftcardEntity buildFromDomain(Giftcard giftcard, SeriesEntity seriesEntity) {
        return new GiftcardEntity(giftcard.cardId(), giftcard.balance(), seriesEntity);
    }
}
