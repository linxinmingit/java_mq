package cn.yr.partitions.Thread;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

public class PartitionerConsumerThread extends Thread{
	/* public static void main(String[] args) {
	    String topic = "HESUI";
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
	    props.put("group.id", "group1");
	    props.put("zookeeper.connect", "192.168.1.8:2181,192.168.1.9:218,192.168.1.10:2181");
	    props.put("zookeeper.session.timeout.ms", "40000");
	    props.put("zookeeper.sync.time.ms", "200");
	    props.put("auto.commit.interval.ms", "1000");
	    props.put("auto.offset.reset", "smallest");
	    return new ConsumerConfig(props);
	}*/
	
	private static  KafkaStream<byte[], byte[]> stream;
	public PartitionerConsumerThread(  KafkaStream<byte[], byte[]> stream) {
	}

	@Override
	public void run() {
		 ConsumerIterator<byte[], byte[]> it = stream.iterator();
	        while (it.hasNext()) {
	            MessageAndMetadata<byte[], byte[]> mam = it.next();
	            System.out.println(Thread.currentThread().getName() + ": partition [" + mam.partition() + "],"+ "offset [" + mam.offset() + "],  " + "Message ["+ new String(mam.message())+" ]");
	        }
	}
}