package dev.jakapaw.giftcardpayment.cardmanager.adapter.kafka;

import dev.jakapaw.giftcardpayment.cardmanager.adapter.kafka.model.VerifyEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProduceVerifyEvent {

    @Autowired
    KafkaTemplate<String, VerifyEvent> verifyEventKafkaTemplate;

    @Autowired
    KafkaConfig kafkaConfig;

    public void publish(VerifyEvent event) {
        verifyEventKafkaTemplate.send(kafkaConfig.giftcardVerified().name(), event.cardId().toString(), event);
    }
}
