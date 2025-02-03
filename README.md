Use the following link to download and install Apache Kafka: 
https://kafka.apache.org/quickstart

tar -xzf kafka_2.13-3.9.0.tgz

The command is tar -xzf where 
x stands for extract, 
z means running the command through gzip, a compression tool, and 
f is the flag you use when you have an archive file, and, finally, the name of the file.

Start the zookeeper:

❯ cd ~/kafka/kafka_2.13-3.9.0
❯ ./bin/zookeeper-server-start.sh config/zookeeper.properties

We are passing the config file as an argument.
By default, the zookeeper server will listen for connections on port 2181.

Now we need to run our brokers. The only way to run two brokers on the same machine is by using two different ports. So I'm going to create a server-1 config and a server-2 config.

❯ cp config/server.properties config/server-1.properties
❯ cp config/server.properties config/server-2.properties

Inside each file, we need to change three properties. 

First, broker.id 
    Each broker should have a unique ID, so the first broker will have an ID of 1. 
    
Second, listeners
    The listeners property is the address where the broker will listen for connections. Since we're not going to use an encrypted connection, the first broker will listen for plaintext on port 9093. 

Third, log.dirs
    It represents the directory where Kafka will store all the messages received. We append -1 to it.

Similarly for the second config file, we used 2 as id and port 9094 for listeners.

Now, we will start the kafka servers (Brokers)

❯ ./bin/kafka-server-start.sh config/server-1.properties
❯ ./bin/kafka-server-start.sh config/server-2.properties

Now, we will create a topic, where we can send information to.

❯ ./bin/kafka-topics.sh --create --bootstrap-server localhost:9093 --partitions 2 --replication-factor 2 --topic user-tracking

We will use the script called kafka-topics, and we need to pass a couple of flags as arguments. 

--create, because we want to create a topic. 

We need to point out one of the brokers from the cluster by using the --bootstrap-server flag. In this case, the address of the broker is localhost on port 9093. 

--partitions 2, We want a topic with two partitions. 
    This will help balance the load across brokers
--replication-factor 2, meaning that for each partition that we have created, we create another replica for it. 
    In total, we would have four partitions, two normal partitions and two replicated partitions. 
    
--topic. Name of the topic.
    Let's call it user-tracking topic. 
    
To check if the topic has been successfully created, we can use the list flag with the same script.

❯ ./bin/kafka-topics.sh --list --bootstrap-server localhost:9093

To delete the topic, we can use the delete flag with the same script.

❯ ./bin/kafka-topics.sh --delete --bootstrap-server localhost:9093 --topic user-tracking