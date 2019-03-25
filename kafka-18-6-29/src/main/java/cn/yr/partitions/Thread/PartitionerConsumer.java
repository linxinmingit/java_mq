package cn.yr.partitions.Thread;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

/**
 * @Date Nov 3, 2016
 *
 * @Author dengjie
 *
 * @Note 通过 Kafka 的消费者 API 验证分区入库的消息
 */
public class PartitionerConsumer extends Thread{
	private static Properties props;
	private static ConsumerConfig consumerConfig;
	private static String topic = "xiongdingkun";
	final static int threads = 6;
	
	
	public PartitionerConsumer() {
         //设置级别属性
		 props = new Properties();
		 props.put("group.id", "111");
	     props.put("zookeeper.connect", "192.168.1.8:2181,192.168.1.9:218,192.168.1.10:2181");
	     props.put("zookeeper.session.timeout.ms", "40000");
	     props.put("zookeeper.sync.time.ms", "200");
	     props.put("auto.commit.interval.ms", "1000");
	     props.put("auto.offset.reset", "smallest");
	     consumerConfig = new ConsumerConfig(props);
	}
	
	@Override
	public void run() {
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
	    topicCountMap.put(topic, new Integer(threads));
	    //consumerConfig = new ConsumerConfig(props);
	    ConsumerConnector consumer = Consumer.createJavaConsumerConnector(consumerConfig);
	    Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
	    List<KafkaStream<byte[], byte[]>> stream = consumerMap.get(topic);
	    ExecutorService executor = Executors.newFixedThreadPool(threads);
	    for (KafkaStream<byte[], byte[]> streams : stream) {
	        executor.submit(new PartitionerConsumerThread(streams));
	    }
	}
	public static void main(String[] args) {
		new PartitionerConsumer().start();
	}
}