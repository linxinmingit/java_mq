package cluster;

import aaa.MQReceive1;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.FileOutputStream;

/**
 *
 * @author
 * @since
 */
public class Mqcustomer1 {
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

    public static void main(String[] args) {
        Mqcustomer1 receive = new Mqcustomer1();
        receive.start();
       // receive.getTextMessage();
        receive.getStreamMessage();
    }

    public void start() {
        try {
            // 根据用户名，密码，url创建一个连接工厂
            factory = new ActiveMQConnectionFactory(userName, password, brokerURL);
            // 从工厂中获取一个连接
            connection = factory.createConnection();
            // 测试过这个步骤不写也是可以的，但是网上的各个文档都写了
            connection.start();
            // 创建一个session
            // 第一个参数:是否支持事务，如果为true，则会忽略第二个参数，被jms服务器设置为SESSION_TRANSACTED
            // 第二个参数为false时，paramB的值可为Session.AUTO_ACKNOWLEDGE，Session.CLIENT_ACKNOWLEDGE，DUPS_OK_ACKNOWLEDGE其中一个。
            // Session.AUTO_ACKNOWLEDGE为自动确认，客户端发送和接收消息不需要做额外的工作。哪怕是接收端发生异常，也会被当作正常发送成功。
            // Session.CLIENT_ACKNOWLEDGE为客户端确认。客户端接收到消息后，必须调用javax.jms.Message的acknowledge方法。jms服务器才会当作发送成功，并删除消息。
            // DUPS_OK_ACKNOWLEDGE允许副本的确认模式。一旦接收方应用程序的方法调用从处理消息处返回，会话对象就会确认消息的接收；而且允许重复确认。
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // //点对点与订阅模式唯一不同的地方，就是这一行代码，点对点创建的是Queue，而订阅模式创建的是Topic
            destination = session.createTopic("HeSui");
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
                        System.out.println("MQCustomer1接收消息:" + text);
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
     * 得到object的消息
     */
    public void getObjectMessage() {
        try {
            // 实现一个消息的监听器
            // 实现这个监听器后，以后只要有消息，就会通过这个监听器接收到
            consumer.setMessageListener(new MessageListener() {
                public void onMessage(Message message) {
                    try {
                        // 获取到接收的数据
                        String text = ((TextMessage) message).getText();
                        System.out.println("MQCustomer1接收消息:" + text);
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
                        System.out.println(" MQCustomer1接收消息 ---->>  int "+mapMessage.getInt("int")+" boolean "+mapMessage.getBoolean("boolean")+" String "+mapMessage.getString("String"));
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
                //FileOutputStream outputStream1 = null;
                FileOutputStream outputStream1 = null;
                public void onMessage(Message message) {
                    try {
                        BytesMessage bytesMessage = (BytesMessage) message;
                        outputStream1 = new FileOutputStream("D:\\用户目录\\下载\\liuwen.txt");
                        byte[] bytes = new byte[1024];
                        int len = 0;
                        while (-1 != (len = bytesMessage.readBytes(bytes))){
                            outputStream1.write(bytes,0,len);
                        }
                        outputStream1.close();
                    }catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }
                }
            });
            System.out.println("MQCustomer1接收消息---->> byte读取消费成功！");
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
                        System.out.println(" MQCustomer1接收消息----->>  字符流 :==>>"+streamMessage.readString()+" 字符流长度==>>："+streamMessage.readLong());
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
