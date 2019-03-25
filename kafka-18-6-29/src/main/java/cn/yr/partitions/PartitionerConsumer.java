package cn.yr.partitions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

/**
 * @Date Oct 3, 2018
 * @Author lin
 * @Note 通过 Kafka 的消费者 API 验证分区入库的消息
 * 
 * 
 * 同一个消费组不能同时消费一个分区的消息 
 * 不同的消费组可以同时消费一个分区的消息
 * 
 * 
 * 
 */
public class PartitionerConsumer {
    public static void main(String[] args) {
        String topic = "xiongdingkun";
        ConsumerConnector consumer = Consumer.createJavaConsumerConnector(createConsumerConfig());
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(1));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        KafkaStream<byte[], byte[]> stream = consumerMap.get(topic).get(0);
        ConsumerIterator<byte[], byte[]> it = stream.iterator();
        while (it.hasNext()) {
            MessageAndMetadata<byte[], byte[]> mam = it.next();
            System.out.println("consume: Partition [" + mam.partition() + "] Message: [" + new String(mam.message())
                    + "] ..");
        }

    }

    private static ConsumerConfig createConsumerConfig() {
        Properties props = new Properties();
        props.put("group.id", "group5");
        props.put("zookeeper.connect", "192.168.1.8:2181,192.168.1.9:218,192.168.1.10:2181");
        props.put("zookeeper.session.timeout.ms", "40000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "smallest");
        return new ConsumerConfig(props);
    }
}