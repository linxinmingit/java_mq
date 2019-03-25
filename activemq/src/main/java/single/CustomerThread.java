package single;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

/**
 *
 * @author
 * @since
 */
public class CustomerThread extends  Thread{

    // 消费者，就是接收数据的对象
    private MessageConsumer consumer;

    @Override
    public void run() {
        try {
            // 实现一个消息的监听器
            // 实现这个监听器后，以后只要有消息，就会通过这个监听器接收到
            consumer.setMessageListener(new MessageListener() {
                public void onMessage(Message message) {
                    try {
                        // 获取到接收的数据
                        String text = ((TextMessage) message).getText();
                        System.out.println("MQCustomer接收消息:" + text);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            // 关闭接收端，也不会终止程序哦
            // consumer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
