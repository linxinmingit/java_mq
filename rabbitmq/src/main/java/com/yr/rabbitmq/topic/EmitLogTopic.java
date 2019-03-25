package com.yr.rabbitmq.topic;
import com.rabbitmq.client.*;

/**
 * @author Administrator
 * @since
 * 
 * 任何发送到Topic Exchange的消息都会被转发到所有关心RouteKey中指定话题的Queue上
  1.这种模式较为复杂，简单来说，就是每个队列都有其关心的主题，所有的消息都带有一个“标题”(RouteKey)，Exchange会将消息转发到所有关注主题能与RouteKey模糊匹配的队列。
  2.这种模式需要RouteKey，也许要提前绑定Exchange与Queue。
  3.在进行绑定时，要提供一个该队列关心的主题，如“#.log.#”表示该队列关心所有涉及log的消息(一个RouteKey为”MQ.log.error”的消息会被转发到该队列)。
  4.“#”表示0个或若干个关键字，“*”表示一个关键字。如“log.*”能与“log.warn”匹配，无法与“log.warn.timeout”匹配；但是“log.#”能与上述两者匹配。
  5.同样，如果Exchange没有发现能够与RouteKey匹配的Queue，则会抛弃此消息。
 */
public class EmitLogTopic {

    private static final String EXCHANGE_NAME = "exchange_topic";
    private static final String[] routingKeys = { "queue.*.insert","logs.delete", "topic.logs.query", "queyue.update","queue.#" };
    private static ConnectionFactory factory;
	private static Connection connection;
	private static Channel channel;
	private static Address address[] = new Address[3];
	
	static{
		try {
			factory = new ConnectionFactory();
			Address addr1 = new Address("192.168.1.181");
			Address addr2 = new Address("192.168.1.182");
			Address addr3 = new Address("192.168.1.183");
			address[0] = addr1;
			address[1] = addr2;
			address[2] = addr3;

			factory.setPort(5672);
			// 设置访问的用户
			factory.setUsername("test");
			factory.setPassword("test");

			connection = factory.newConnection(address);
			channel = connection.createChannel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public static void main(String[] argv) throws Exception {
    		EmitLogTopic.method1();
    }
    public static void method1() throws Exception
	{
	        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
	        String message = getMessage("hello");
	        //分别推送"adtec.laoliu","ali.xiaowang", "tencent.laozhang", "adtec.xiaohu"消息
	        for (String routingKey : routingKeys) {
	            String msg = message+ "' from "+routingKey;
	            channel.basicPublish(EXCHANGE_NAME, routingKey, null, msg.getBytes());
	            System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");
	        }
	        
	        //每个队列都会拿到4个key的值
	       /* for (int j = 1; j < 3; j++) {
	        	String queue = "topic-"+j;
	        	//声明队列
		        	for (String routingKey : routingKeys) {
		        		channel.queueDeclare(queue, false, false, false, null);
		        		//绑带队列
		        		channel.queueBind(queue, EXCHANGE_NAME, routingKey);
		        		
		                String msg = message+ "' from "+routingKey;
		                channel.basicPublish(EXCHANGE_NAME, routingKey, null, msg.getBytes());
		                System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");
		            }
			}*/
	        channel.close();
 	        connection.close();
	       
	}
    
    private static String getMessage(String strings) {

        return "info: Hello World!";
    }
}
