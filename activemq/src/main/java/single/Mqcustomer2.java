package single;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.FileOutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author
 * @since
 */
public class Mqcustomer2{
    // 连接账号
    private String userName = "";
    // 连接密码
    private String password = "";
    // 连接地址
    private String brokerURL = "tcp://192.168.1.77:61616";
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

    public static void main(String[] args) {
        Mqcustomer2 receive = new Mqcustomer2();
        receive.start();
        receive.getTextMessage();
       // receive.getObjectMessage();
       // receive.getMapMessage();
       // receive.getByteMessage();
       // receive.getStreamMessage();

      /*  ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 1; i < 3; i++) {
            cachedThreadPool.execute(new CustomerThread());
        }*/
    }

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
            destination = session.createTopic("Alen Walker");
            //Queue qu = session.createQueue("aaa");

            //Queue q = session.createQueue("liuwen");

            // 根据session，创建一个接收者对象
            consumer = session.createConsumer(destination);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到txt的消息
     */
    public void getTextMessage() {
        try {
            // 实现一个消息的监听器
            // 实现这个监听器后，以后只要有消息，就会通过这个监听器接收到
            consumer.setMessageListener(new MessageListener() {
                public void onMessage(Message message) {
                    try {
                        // 获取到接收的数据
                        String text = ((TextMessage) message).getText();
                        System.out.println("MQCustomer2接收消息:" + text);
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
    /**
     * 得到Object的消息
     */
    public void getObjectMessage() {
        try {
            // 实现一个消息的监听器
            // 实现这个监听器后，以后只要有消息，就会通过这个监听器接收到
                consumer.setMessageListener(new MessageListener() {
                public void onMessage(Message message) {
                    try {
                        // 获取到接收的数据
                        //String text = ((TextMessage) message).getText();
                        Student student = (Student) ((ObjectMessage) message).getObject();
                        System.out.println("MQCustomer2接收消息--->> :" + student.toString());
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

    /**
     * map
     */
    public  void getMapMessage(){
        try {
            consumer.setMessageListener(new MessageListener() {
                public void onMessage(Message message) {
                    //((MapMessage)message).getMapNames();
                    MapMessage mapMessage = (MapMessage) message;
                    try {
                        System.out.println(" MQCustomer2接收消息----->> int "+mapMessage.getInt("int")+" boolean "+mapMessage.getBoolean("boolean")+" String "+mapMessage.getString("String"));
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * byte
     */
    public void getByteMessage(){
        try {
            consumer.setMessageListener(new MessageListener() {
                FileOutputStream outputStream = null;
                public void onMessage(Message message) {
                    try {
                        BytesMessage bytesMessage = (BytesMessage) message;
                        outputStream = new FileOutputStream("D:\\用户目录\\下载\\liuwen.txt");
                        byte[] bytes = new byte[1024];
                        int len = 0;
                        while (-1 != (len = bytesMessage.readBytes(bytes))){
                            outputStream.write(bytes,0,len);
                        }
                        outputStream.close();
                    }catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }
                }
            });
            System.out.println("MQCustomer2接收消息----->>  byte读取消费成功！");
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * stream
     */
    public  void  getStreamMessage()
    {
        try {
            consumer.setMessageListener(new MessageListener() {
                public void onMessage(Message message) {
                    StreamMessage streamMessage = (StreamMessage) message;
                    try {
                        System.out.println("MQCustomer2接收消息 ---->> 字符流 :==>>"+streamMessage.readString()+" 字符流长度==>>："+streamMessage.readLong());
                    }catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }
                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
