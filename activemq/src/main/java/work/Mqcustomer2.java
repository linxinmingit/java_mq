package work;

import cluster.Student;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author
 * @since
 */
public class Mqcustomer2 {
    // 连接账号
    private String userName = "";
    // 连接密码
    private String password = "";
    // 连接地址
    private String brokerURL = "failover:(tcp://192.168.1.161:61616,tcp://192.168.1.162:61616,tcp://192.168.1.163:61616)";
    // connection的工厂
    private ConnectionFactory factory;
    // 连接对象
    private Connection connection;
    // 一个操作会话
    private Session session;
    // 目的地，其实就是连接到哪个队列，如果是点对点，那么它的实现是Queue，如果是订阅模式，那它的实现是Topic
    private Destination destination;
    // 消费者，就是接收数据的对象
    private MessageConsumer consumer;
    public void start() {
        try {
            // 根据用户名，密码，url创建一个连接工厂
            factory = new ActiveMQConnectionFactory(userName, password, brokerURL);
            // 从工厂中获取一个连接
            connection = factory.createConnection();
            // 测试过这个步骤不写也是可以的，但是网上的各个文档都写了
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // //点对点与订阅模式唯一不同的地方，就是这一行代码，点对点创建的是Queue，而订阅模式创建的是Topic
           // destination = session.createTopic("HeSui");

            Queue q = session.createQueue("liuwen");

            // 根据session，创建一个接收者对象
            consumer = session.createConsumer(q);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Mqcustomer2 receive = new Mqcustomer2();
        receive.start();
        //receive.getTextMessage();
        ExecutorService threadPool= Executors.newFixedThreadPool(2);
        threadPool.execute(new Thread1(receive.consumer));
        threadPool.execute(new Thread2(receive.consumer));
    }
}
