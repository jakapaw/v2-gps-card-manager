package dev.jakapaw.giftcardpayment.cardmanager.application.port.in;

import dev.jakapaw.giftcardpayment.cardmanager.application.event.PaymentAccepted;
import dev.jakapaw.giftcardpayment.cardmanager.application.event.PaymentDeclined;
import org.springframework.context.event.EventListener;

public interface ListenPaymentEvents {

    public void on(PaymentAccepted event);
    public void on(PaymentDeclined event);
}
