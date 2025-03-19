package com.example.processor;

import com.example.model.Activity;
import com.example.model.Profile;
import com.example.model.ErrorMessage;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class EnrichmentProcessor {

    private final String errorTopic;

    public EnrichmentProcessor(String errorTopic) {
        this.errorTopic = errorTopic;
    }

    public void enrichStream(KStream<String, Activity> activityStream, 
                           KTable<String, Profile> profileTable) {
        
        activityStream
            .leftJoin(
                profileTable,
                this::enrichActivity,
                Joined.with(
                    org.apache.kafka.common.serialization.Serdes.String(),
                    null,  // Activity Serde will be configured by Spring
                    null   // Profile Serde will be configured by Spring
                )
            )
            .transformValues(() -> new ValueTransformerWithKey<String, Activity, Activity>() {
                private ProcessorContext context;

                @Override
                public void init(ProcessorContext context) {
                    this.context = context;
                }

                @Override
                public Activity transform(String key, Activity value) {
                    try {
                        return value;
                    } catch (Exception e) {
                        ErrorMessage error = ErrorMessage.newBuilder()
                            .setOriginalKey(key)
                            .setOriginalValue(value.toString().getBytes())
                            .setErrorMessage(e.getMessage())
                            .setTimestamp(Instant.now().toEpochMilli())
                            .setRetryCount(0)
                            .build();
                        
                        context.forward(key, error, errorTopic);
                        return null;
                    }
                }

                @Override
                public void close() {}
            });
    }

    private Activity enrichActivity(Activity activity, Profile profile) {
        if (profile == null) {
            return activity;
        }

        // Enrich activity with profile data
        activity.getData().put("userName", profile.getName());
        activity.getData().put("userEmail", profile.getEmail());
        activity.getData().putAll(profile.getAttributes());
        
        return activity;
    }
} 