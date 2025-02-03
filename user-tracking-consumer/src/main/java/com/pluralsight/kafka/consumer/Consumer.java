package com.pluralsight.kafka.consumer;

import static java.util.Arrays.asList;

import java.time.Duration;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Consumer {

    public static void main(String[] args) {

        SuggestionEngine suggestionEngine = new SuggestionEngine();

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9093, localhost:9094");
        properties.put("group.id", "user-tracking-consumer");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        // The same consumer can subscribe to multiple topics, but in this case we are
        // only interested in the user-tracking topic.
        // so we need to implement the logic to process the messages from this topic.
        consumer.subscribe(asList("user-tracking"));


        // A pull operation will only happen once, so in order to keep the application running, 
        // We are going to wrap this code in an infinite while loop.
        while (true) {
            // To receive the records from Kafka, we need to ask the consumer to pull for
            // 100 milliseconds.
            // The pull duration represents the time that a consumer will keep the
            // connection open with the broker and receives the record.
            // After the duration is passed, we are able to access the consumer records and
            // process them.
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

            records.forEach(record -> {
                log.info("Consuming record: " + record);
                suggestionEngine.processSuggestions(record.key(), record.value());
            });
        }
    }
}
