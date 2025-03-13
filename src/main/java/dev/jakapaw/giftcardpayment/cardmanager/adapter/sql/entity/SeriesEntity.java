package dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.entity;

import dev.jakapaw.giftcardpayment.cardmanager.application.domain.Series;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "series")
public class SeriesEntity extends AbstractEntity<String> {

    @Id
    String seriesId;

    @OneToMany(mappedBy = "seriesId")
    List<GiftcardEntity> giftcards;

    @NotNull @Positive
    Integer totalCards;

    @NotNull @Positive
    Long totalValue;

    @NotNull
    LocalDateTime createdAt;

    @Override
    public String getId() {
        return seriesId;
    }

    public static SeriesEntity buildFromDomain(Series series) {
        return new SeriesEntity(
                series.seriesId(),
                null,
                series.totalCards(),
                series.totalValue(),
                series.createdAt());
    }
}
