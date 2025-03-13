package dev.jakapaw.giftcardpayment.cardmanager.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.GiftcardDAO;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.GiftcardEventSourcing;
import dev.jakapaw.giftcardpayment.cardmanager.adapter.sql.SeriesDAO;
import dev.jakapaw.giftcardpayment.cardmanager.application.command.CreateSeriesCommand;
import dev.jakapaw.giftcardpayment.cardmanager.application.command.VerifyGiftcardCommand;
import dev.jakapaw.giftcardpayment.cardmanager.application.domain.Giftcard;
import dev.jakapaw.giftcardpayment.cardmanager.application.domain.Series;
import dev.jakapaw.giftcardpayment.cardmanager.application.event.GiftcardCreated;
import dev.jakapaw.giftcardpayment.cardmanager.application.port.in.ManageGiftcard;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ManageGiftcardDefault implements ManageGiftcard {

    GiftcardDAO giftcardDAO;
    GiftcardEventSourcing giftcardEventSourcing;
    ObjectMapper om;

    public ManageGiftcardDefault(GiftcardDAO giftcardDAO, GiftcardEventSourcing giftcardEventSourcing, ObjectMapper om) {
        this.giftcardDAO = giftcardDAO;
        this.giftcardEventSourcing = giftcardEventSourcing;
        this.om = om;
    }

    @Override
    public String createSeries(CreateSeriesCommand command) {
        Series series = new Series(
                command.issuerId(),
                command.totalCards(),
                command.totalValue(),
                command.issuerUnique()
        );
        List<Giftcard> giftcards = series.giftcards();
        giftcardDAO.saveAll(giftcards, series);

        for (var el : giftcards) {
            try {
                giftcardEventSourcing.pushGiftcardCreated(
                        el.cardId(),
                        om.writeValueAsString(el),
                        el.balance(),
                        GiftcardCreated.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return series.seriesId();
    }

    @Override
    public void verifyGiftcard(VerifyGiftcardCommand command) {
        giftcardEventSourcing.rebuildState(command.cardId()).orElseThrow()
                .verifyCardId(command.cardId())
                .verifySufficeBalance(command.totalBilled());
    }
}
