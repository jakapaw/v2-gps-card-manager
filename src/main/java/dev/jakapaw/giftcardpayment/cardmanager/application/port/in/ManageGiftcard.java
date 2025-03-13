package dev.jakapaw.giftcardpayment.cardmanager.application.port.in;

import dev.jakapaw.giftcardpayment.cardmanager.application.command.CreateSeriesCommand;
import dev.jakapaw.giftcardpayment.cardmanager.application.command.VerifyGiftcardCommand;

public interface ManageGiftcard {

    public String createSeries(CreateSeriesCommand command);

    public void verifyGiftcard(VerifyGiftcardCommand command);
}
