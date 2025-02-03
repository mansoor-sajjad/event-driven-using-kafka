package com.pluralsight.kafka.producer;


import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.Properties;

import com.pluralsight.kafka.model.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.pluralsight.kafka.producer.model.Event;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Producer {

    public static void main(String[] args) throws InterruptedException {
        
        EventGenerator eventGenerator = new EventGenerator();
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9093, localhost:9094");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.KafkaAvroSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.KafkaAvroSerializer");
        properties.put("schema.registry.url", "http://localhost:8081");

        org.apache.kafka.clients.producer.Producer<User, Product> producer = new KafkaProducer<>(properties);

        for (int i = 0; i <= 10; i++) {
            log.info("Generating event number: " + i);

            Event event = eventGenerator.generateEvent();

            User key = extractKey(event);
            Product value = extractValue(event);

            ProducerRecord<User, Product> record = new ProducerRecord<>("user-tracking-avro", key, value);
            log.info("Producing the record to kafka: " + event);

            log.info("Producing the record to Kafka wit key: " + key + ":" + value);
            producer.send(record);

            sleep(1000);
        }
        // We need to close the connection to the Kafka cluster, to avoid memory leaks.
        producer.close();
    }
                                                
    private static void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static User extractKey(Event event) {
        return User.newBuilder()
                .setUserId(event.getInternalUser().getUserId().toString())
                .setUsername(event.getInternalUser().getUsername())
                .setDateOfBirth((int)event.getInternalUser().getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).getLong(ChronoField.EPOCH_DAY))
                .build();
    }

    private static Product extractValue(Event event) {
        return Product.newBuilder()
                .setProductType(ProductType.valueOf(event.getInternalProduct().getType().name()))
                .setColor(Color.valueOf(event.getInternalProduct().getColor().name()))
                .setDesignType(DesignType.valueOf(event.getInternalProduct().getDesignType().name()))
                .build();
    }

}
