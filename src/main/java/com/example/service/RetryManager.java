package com.example.service;

import com.example.model.ErrorMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class RetryManager {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String errorTopic;
    private final String inputTopic;
    private final int maxRetries;
    
    public RetryManager(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.kafka.topics.error}") String errorTopic,
            @Value("${app.kafka.topics.input-stream}") String inputTopic,
            @Value("${app.kafka.retry.max-attempts}") int maxRetries) {
        this.kafkaTemplate = kafkaTemplate;
        this.errorTopic = errorTopic;
        this.inputTopic = inputTopic;
        this.maxRetries = maxRetries;
    }

    @Scheduled(fixedRateString = "${app.kafka.retry.interval-minutes}", timeUnit = TimeUnit.MINUTES)
    public void processRetries() {
        kafkaTemplate.receive(errorTopic, 0, 0)
            .forEach(this::processErrorRecord);
    }

    private void processErrorRecord(ConsumerRecord<String, ErrorMessage> record) {
        ErrorMessage error = record.value();
        
        if (error.getRetryCount() >= maxRetries) {
            // Log permanent failure
            return;
        }

        // Update retry count and timestamp
        error = ErrorMessage.newBuilder(error)
            .setRetryCount(error.getRetryCount() + 1)
            .setLastRetryTime(Instant.now().toEpochMilli())
            .build();

        // Send back to input topic for reprocessing
        kafkaTemplate.send(new ProducerRecord<>(inputTopic, 
            error.getOriginalKey(), 
            new String(error.getOriginalValue())));

        // Update error record
        kafkaTemplate.send(new ProducerRecord<>(errorTopic, 
            record.key(), 
            error));
    }
} 