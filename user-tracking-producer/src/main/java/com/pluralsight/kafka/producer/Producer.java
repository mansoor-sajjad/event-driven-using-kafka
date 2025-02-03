package com.pluralsight.kafka.producer;


import java.util.Properties;

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
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        org.apache.kafka.clients.producer.Producer<String, String> producer = new KafkaProducer<>(properties);

        for (int i = 0; i <= 10; i++) {
            log.info("Generating event number: " + i);

            Event event = eventGenerator.generateEvent();

            String key = extractKey(event);
            String value = extractValue(event);

            ProducerRecord<String, String> record = new ProducerRecord<>("user-tracking", key, value);
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

    private static String extractKey(Event event) {
        return event.getUser().getUserId().toString();
    }
            
    private static String extractValue(Event event) {
        return String.format("%s, %s, %s", event.getProduct().getType(), event.getProduct().getColor(), event.getProduct().getDesignType());
    }

}
