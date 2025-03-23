package dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.event;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "card_id_unique", columnNames = { "cardId" })
})
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class GiftcardEventSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long snapshotId;

    private Long cardId;
    private Integer lastVersion;
    private Long balance;

    public GiftcardEventSnapshot(Long snapshotId, Integer lastVersion, Long balance) {
        this.snapshotId = snapshotId;
        this.lastVersion = lastVersion;
        this.balance = balance;
    }
}