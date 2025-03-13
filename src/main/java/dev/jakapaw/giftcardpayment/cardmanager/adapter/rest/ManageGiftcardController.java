package dev.jakapaw.giftcardpayment.cardmanager.adapter.rest;

import dev.jakapaw.giftcardpayment.cardmanager.adapter.rest.model.CreateSeriesDTO;
import dev.jakapaw.giftcardpayment.cardmanager.application.command.CreateSeriesCommand;
import dev.jakapaw.giftcardpayment.cardmanager.application.port.in.ManageGiftcard;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ManageGiftcardController {

    ManageGiftcard manageGiftcard;

    public ManageGiftcardController(ManageGiftcard manageGiftcard) {
        this.manageGiftcard = manageGiftcard;
    }

    @PostMapping("/create")
    public String createSeries(@RequestBody CreateSeriesDTO body) {
        CreateSeriesCommand command = new CreateSeriesCommand(
                body.issuerId(), body.totalCards(), body.totalValue(), body.issuerUnique());
        return manageGiftcard.createSeries(command);
    }
}
