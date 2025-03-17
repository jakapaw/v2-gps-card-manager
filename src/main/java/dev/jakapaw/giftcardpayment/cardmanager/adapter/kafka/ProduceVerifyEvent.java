package dev.jakapaw.giftcardpayment.cardmanager.adapter.kafka;

import dev.jakapaw.giftcardpayment.cardmanager.adapter.kafka.model.VerifyEvent;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class ProduceVerifyEvent {

    @Autowired
    KafkaTemplate<String, VerifyEvent> verifyEventKafkaTemplate;

    @Autowired
    KafkaConfig kafkaConfig;

    public void publish(VerifyEvent event) {
        ContextPropagators contextPropagators = ContextPropagators.create(W3CTraceContextPropagator.getInstance());
        TextMapSetter<ProducerRecord<String, VerifyEvent>> textMapSetter = new KafkaHeaderSetter<>();

        ProducerRecord<String, VerifyEvent> record =
                new ProducerRecord<>(kafkaConfig.giftcardVerified().name(), event.cardId().toString(), event);

        // Inject context into kafka ProducerRecord
        contextPropagators.getTextMapPropagator().inject(Context.current(), record, textMapSetter);

        verifyEventKafkaTemplate.send(kafkaConfig.giftcardVerified().name(), event.cardId().toString(), event);
    }

    private static class KafkaHeaderSetter<K, V> implements TextMapSetter<ProducerRecord<K, V>> {
        @Override
        public void set(ProducerRecord<K, V> carrier, String key, String val) {
            if (carrier == null) {
                throw new IllegalArgumentException("Carrier must not be null");
            }
            carrier.headers().add(key, val.getBytes(StandardCharsets.UTF_8));
        }
    }
}
