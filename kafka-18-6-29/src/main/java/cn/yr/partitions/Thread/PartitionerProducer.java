package cn.yr.partitions.Thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * @Date Nov 3, 2016
 *
 * 
 * @Note 按照先 Hash 再取模的规则，进行分区入库
 */
public class PartitionerProducer extends Thread {
	final static long values = 100L;
	final static Random rnd = new Random();
	final static String topic = "Kloss";
    private static  Producer<String, String> producer = null;
	private static ProducerConfig config = null;

	//构造方法
	public PartitionerProducer() {
		Properties props = new Properties();
		// 主机地址+端口号
		props.put("metadata.broker.list", "192.168.1.8:9092,192.168.1.9:9092,192.168.1.10:9092");
		// 消息序列化类
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		// 同步还是异步，默认2表同步，1表异步。异步可以提高发送吞吐量，但是也可能导致丢失未发送过去的消息
		//props.put("producer.type", "sync");
		//自定义分区
		props.put("partitioner.class", "cn.yr.partitions.Thread.CustomerPartitioner");
		props.put("zookeeper.session.timeout.ms", "40000");
		
		 props.put("zookeeper.sync.time.ms", "2000");
		 props.put("auto.commit.interval.ms", "1000");
		 props.put("enable.auto.commit", "false");
		 
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		config = new ProducerConfig(props);
	}
	@Override
	public void run() {
		producer = new Producer<String, String>(config);
		for (int mapvalue = 0; mapvalue < values; mapvalue++) {
			String k = "192.168.2." + rnd.nextInt(255); // 随机数 并不是将东西发送到某个ip里
			String v = String.valueOf(mapvalue);
			producer.send(new KeyedMessage<String, String>(topic, k, v));
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		producer.close();
	}
	
	public static void main(String[] args) {
		 //线程池允许同时存在3个线程
       /* ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(3);
        for (int ins = 1; ins < 4; ins++) {
			//int index = ins;
			newFixedThreadPool.execute(new PartitionerProducer());
		}*/
		new PartitionerProducer().start();
	}

}