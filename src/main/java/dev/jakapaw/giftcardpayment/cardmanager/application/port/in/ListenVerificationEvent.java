package dev.jakapaw.giftcardpayment.cardmanager.application.port.in;

import dev.jakapaw.giftcardpayment.cardmanager.application.event.VerificationDone;

public interface ListenVerificationEvent {

    public void on(VerificationDone event);
}
