package work;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 *
 * @author
 * @since
 */
public class Thread2 extends Thread{
    // 消费者，就是接收数据的对象
    private MessageConsumer consumer;
    public Thread2(MessageConsumer consumer) {
        this.consumer = consumer;
    }

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
                        System.out.println(Thread.currentThread().getName()+"-->>接收消息-->>:" + text+ "         >>"+text+Thread.currentThread().hashCode());
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
