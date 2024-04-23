package com.example.bookmyshow.service;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.bookmyshow.repository.TicketUpdateRedisRepository;
import redis.clients.jedis.Jedis;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

@Component
public class TicketEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TicketEventConsumer.class);

    private final KafkaConsumer<String, String> consumer;
    @Autowired
    private  Jedis jedis;

    @Autowired
    private TicketUpdateRedisRepository ticketUpdateRedisRepository;


    @PostConstruct
    void startConsumer()
    {
        Thread thread = new Thread(this::pollEventsAndUpdateRedis);
        thread.start();
    }

    public TicketEventConsumer() {
        Properties kafkaProps = new Properties();
        kafkaProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        kafkaProps.put(ConsumerConfig.GROUP_ID_CONFIG, "ticket-events-group");
        kafkaProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        kafkaProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumer = new KafkaConsumer<>(kafkaProps);
        consumer.subscribe(Arrays.asList("ticket-booked-topic","ticket-cancelled-topic"));
    }


    public void pollEventsAndUpdateRedis() {
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    String topic = record.topic();
                    String message = record.value();
                    logger.info("Received message from topic {}: {}", topic, message);

                    updateRedisWithTicketEvent(topic, message);
                }
            }
        } catch (Exception e) {
            logger.error("Error while polling events and updating Redis", e);
        } finally {
            close();
        }
    }

    private void updateRedisWithTicketEvent(String topic, String message) {
        String redisKey;
        if ("ticket-booked-topic".equals(topic)) {
            logger.info("Updating Redis with ticket booked event: {}", message);
            ticketUpdateRedisRepository.totalBookedTicket(Long.parseLong(message));
        } else if ("ticket-cancelled-topic".equals(topic)) {
            logger.info("Updating Redis with ticket cancelled event: {}", message);
            ticketUpdateRedisRepository.totalCanceledTicket(Long.parseLong(message));
        } else {
            logger.warn("Received message from unknown topic: {}", topic);
            return; // Ignore messages from other topics
        }
    }

    public void close() {
        consumer.close();
    }
}
