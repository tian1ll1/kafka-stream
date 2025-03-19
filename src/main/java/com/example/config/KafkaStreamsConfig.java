package com.example.config;

import org.apache.kafka.streams.StreamsConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamsConfig {

    @Bean
    @ConfigurationProperties(prefix = "kafka.streams")
    public KafkaStreamsConfiguration kafkaStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "${kafka.bootstrap-servers}");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "${kafka.streams.application-id}");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, 
                 "org.apache.kafka.common.serialization.Serdes$StringSerde");
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, 
                 "io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde");
        props.put("schema.registry.url", "${kafka.streams.properties.schema.registry.url}");
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, 
                 "${kafka.streams.properties.processing.guarantee}");

        return new KafkaStreamsConfiguration(props);
    }
} 