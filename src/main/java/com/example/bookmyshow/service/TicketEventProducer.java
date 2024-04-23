package com.example.bookmyshow.service;

import com.example.bookmyshow.config.KafkaConfigLoader;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

@Component
public class TicketEventProducer {

    private final KafkaProducer<String, String> producer;

    public TicketEventProducer() throws IOException {
        Properties kafkaProps = new KafkaConfigLoader().loadConfig();
        producer = new KafkaProducer<>(kafkaProps);
    }

    public void sendTicketBookedEvent(String message) {
        producer.send(new ProducerRecord<>("ticket-booked-topic",String.valueOf(message)));
    }

    public void sendTicketCancelledEvent(String message) {
        producer.send(new ProducerRecord<>("ticket-cancelled-topic",String.valueOf(message)));
    }

    public void close() {
        producer.close();
    }
}
