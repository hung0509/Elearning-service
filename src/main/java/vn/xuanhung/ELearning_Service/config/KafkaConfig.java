package vn.xuanhung.ELearning_Service.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Value("${kafka.hostname}")
    String hostname;

    @Value("${kafka.port}")
    String port;

    @Value("${kafka.group-id}")
    String groupId;

    @Bean
    KafkaTemplate<String, Object> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory(){
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, hostname + ":" + port);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);  // Retry if sending fails
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1); // Batch messages for performance
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 2000); // Request timeout in milliseconds
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 2001); // Delivery timeout in milliseconds
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 2000);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory(){
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hostname + ":" + port);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); // Start from beginning if no offset
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // Handle commits manually
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10); // Limit records per poll to avoid long processing
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000); // Heartbeat session timeout
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 5000); // Frequency of heartbeats
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000); // Max interval for message processing

        return new DefaultKafkaConsumerFactory<>(props);
    }

    //Thiết lập số consumer đồng thời nhận topic
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory());
//        factory.setConcurrency(3); // Số lượng luồng để xử lý song song
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL); // Để xác nhận thủ công
//        return factory;
//    }

}
