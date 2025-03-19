package com.example.controller;

import org.apache.kafka.streams.KafkaStreams;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    private final KafkaStreams kafkaStreams;

    public HealthController(KafkaStreams kafkaStreams) {
        this.kafkaStreams = kafkaStreams;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("kafka_streams_state", kafkaStreams.state().name());
        health.put("metrics", kafkaStreams.metrics());
        return health;
    }
} 