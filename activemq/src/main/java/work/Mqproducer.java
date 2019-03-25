package work;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Mqproducer {

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
    // 生产者，就是产生数据的对象
    private MessageProducer producer;

    public static void main(String[] args) {
        Mqproducer send = new Mqproducer();
        send.start();
        send.sendTextMessage();
    }

    public void start() {
        try {
            // 根据用户名，密码，url创建一个连接工厂
            factory = new ActiveMQConnectionFactory(userName, password, brokerURL);
            // 从工厂中获取一个连接
            connection = factory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // //点对点与订阅模式唯一不同的地方，就是这一行代码，点对点创建的是Queue，而订阅模式创建的是Topic
           // destination = session.createTopic("HeSui");
           Queue queue = session.createQueue("liuwen");
            // 从session中，获取一个消息生产者
            producer = session.createProducer(queue);
            // 设置生产者的模式，有两种可选
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送text消息
     */
    public void sendTextMessage() {
        try {
            // 创建一条消息，当然，消息的类型有很多，如文字，字节，对象等,可以通过session.create..方法来创建出来
            TextMessage textMsg = session.createTextMessage();
            for (int i = 0; i < 100; i++) {
                textMsg.setText("测试发送消息 " + i);
                producer.send(textMsg);// 发送一条消息
            }
            System.out.println("发布消息成功");
            // 即便生产者的对象关闭了，程序还在运行哦 producer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

