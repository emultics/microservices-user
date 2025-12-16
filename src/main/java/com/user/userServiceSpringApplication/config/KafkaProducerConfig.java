package com.user.userServiceSpringApplication.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {
    @Value("${kafka.topic.name}")
    private String topicName;

    @Value("${kafka.topic.partitions}")
    private String partitionCount;

    @Value("${kafka.topic.replication.factor}")
    private String replicationFactor;

    @Bean
    public NewTopic userTopic(){
        return TopicBuilder.name(topicName)
                .partitions(Integer.parseInt(partitionCount))
                .replicas(Integer.parseInt(replicationFactor))
                .build();
    }
    @Bean
    public KafkaTemplate<String,String> kafkaTemplate(ProducerFactory<String,String> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }
}
