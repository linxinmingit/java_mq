package consumer.group;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
 
/**
 * 消费者
 * @author zxy
 *
 */
public class ConsumerTest implements Runnable {
    private KafkaStream m_stream;
    private int m_threadNumber;
 
    public ConsumerTest(KafkaStream a_stream, int a_threadNumber) {
        m_threadNumber = a_threadNumber;
        m_stream = a_stream;
    }
 
    public void run() {
        ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
        while (it.hasNext()){
            //System.out.println( new String(it.next().message()));
        	MessageAndMetadata<byte[], byte[]> mam = it.next();
            System.out.println("consume: Partition [" + mam.partition() + "] Message: [" + new String(mam.message())
                    + "] ..");
			
        }
        System.out.println("Shutting down Thread: " + m_threadNumber);
    }
}