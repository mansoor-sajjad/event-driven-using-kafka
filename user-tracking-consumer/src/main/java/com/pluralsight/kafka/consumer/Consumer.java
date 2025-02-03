package com.pluralsight.kafka.consumer;

import static java.util.Arrays.asList;

import java.time.Duration;
import java.util.Properties;

import com.pluralsight.kafka.model.Product;
import com.pluralsight.kafka.model.User;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Consumer {

    public static void main(String[] args) {

        SuggestionEngine suggestionEngine = new SuggestionEngine();

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9093, localhost:9094");

        // The group.id is useful when we want to share the load of messages across multiple consumers without having to deal with duplicate messages.
        // Each consumer should be a part of a consumer group.
        // If multiple consumers are part of the same consumer group, then they will share their load of messages, and they will act as a single consumer.
        properties.put("group.id", "user-tracking-consumer");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("specific.avro.reader", "true"); // Must be set to true, in order to cast the record to the correct type.
        properties.put("schema.registry.url", "http://localhost:8081");

        KafkaConsumer<User, Product> consumer = new KafkaConsumer<>(properties);

        // The same consumer can subscribe to multiple topics, but in this case we are
        // only interested in the user-tracking topic.
        // so we need to implement the logic to process the messages from this topic.
        consumer.subscribe(asList("user-tracking-avro"));


        // A pull operation will only happen once, so in order to keep the application running, 
        // We are going to wrap this code in an infinite while loop.
        while (true) {
            // To receive the records from Kafka, we need to ask the consumer to pull for
            // 100 milliseconds.
            // The pull duration represents the time that a consumer will keep the
            // connection open with the broker and receives the record.
            // After the duration is passed, we are able to access the consumer records and
            // process them.
            ConsumerRecords<User, Product> records = consumer.poll(Duration.ofMillis(100));

            records.forEach(record -> {
                log.info("Consuming record: " + record);
                suggestionEngine.processSuggestions(record.key(), record.value());
            });
        }
    }
}
