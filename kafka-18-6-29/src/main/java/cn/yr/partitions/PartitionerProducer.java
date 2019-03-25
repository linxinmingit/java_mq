package cn.yr.partitions;
import java.util.Properties;
import java.util.Random;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * @Date Nov 3, 2016

 *
 * @Note 按照先 Hash 再取模的规则，进行分区入库
 */
public class PartitionerProducer {
	final static long values = 100L;
	final static Random rnd = new Random();
	final static String topic = "xiongdingkun";
	
    public static void main(String[] args) {
        producerData();
    }

    private static void producerData() {
        Properties props = new Properties();
        // 主机地址+端口号
        props.put("metadata.broker.list", "192.168.1.8:9092,192.168.1.9:9092,192.168.1.10:9092"); 
        // 消息序列化类
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("partitioner.class", "cn.yr.partitions.CustomerPartitioner");
        props.put("zookeeper.session.timeout.ms", "40000");
       /* props.put("zookeeper.sync.time.ms", "2000");
        props.put("auto.commit.interval.ms", "1000");
        props.put("enable.auto.commit", "false");*/
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        Producer<String, String> producer = new Producer<String, String>(new ProducerConfig(props));
        for (int mapvalue = 1; mapvalue < values; mapvalue++) {
			//String k = "key"+mapvalue;
        	String k = "192.168.2." + rnd.nextInt(255); // 随机数  并不是将东西发送到某个ip里
			String v = String.valueOf(mapvalue);
			producer.send(new KeyedMessage<String, String>(topic, k, v));
			
			/*try {
				Thread.sleep(1000*1);
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			//System.out.println("[key = "+k+"   , [values = "+ v+" ]");
		}
        producer.close();
    }
}