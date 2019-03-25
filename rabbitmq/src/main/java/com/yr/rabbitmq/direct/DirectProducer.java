package com.yr.rabbitmq.direct;
 
import com.rabbitmq.client.Address;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
 
/**
 * 
 * @author Administrator
 * @since
 * 
 * 发送到类型为direct的exchange的消息，会根据routingkey来判断消息发送给哪个queue。
 * 每个绑定该exchange的queue都需要绑定一个routingkey。如果接收到的消息不符合绑定的每一个routingkey
 * 则消息会被抛弃掉。
 * 
 */
public class DirectProducer {
 
	private static final String EXCHANGE_NAME = "exchange_direct";
 
	public static void main(String[] argv) throws Exception{
		/*for(int i=1;i<10;i++)
		{
		   new ExchangeDirect("logs.direct.logs", "logs Info test ！！");
		   new ExchangeDirect("queue.#", "logs Info test ！！");
		}*/
		new ExchangeDirect("logs.direct.logs", "logs Info test ！！");
		new ExchangeDirect("direct.queue.#", "logs Info test ！！");
	}
	
	static class ExchangeDirect{
		public ExchangeDirect(String routingKey,String message) throws Exception{
			ConnectionFactory factory = new ConnectionFactory();

			Address address[] = new Address[3];
			Address addr1 = new Address("192.168.1.181");
			Address addr2 = new Address("192.168.1.182");
			Address addr3 = new Address("192.168.1.183");
			address[0] = addr1;
			address[1] = addr2;
			address[2] = addr3;
			
			//rabbitmq监听默认端口
			factory.setPort(5672);
			//设置访问的用户
			factory.setUsername("test");
			factory.setPassword("test");
			Connection connection = factory.newConnection(address);
			Channel channel = connection.createChannel();
 
			//声明路由名字和类型
			channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true, false, null);
			//队列名称
			
			
			String queueName = routingKey + ".queue";
			//创建队列
			channel.queueDeclare(queueName, false, false, false, null);
			//把队列绑定到路由上
			channel.queueBind(queueName, EXCHANGE_NAME, routingKey);
			
			channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
			
			
			/*int scoure = (int) ((Math.random()*100)+1);//获取1~100的随机数
			System.out.println("生成的随机数为 : "+scoure);
			if(scoure < 60)
			{
				//创建（声明队列）
				channel.queueDeclare("direct-1", false, false, false, null);
				//队列与交换机绑定
				channel.queueBind("direct-1", EXCHANGE_NAME, routingKey);
				channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
			}else if(scoure >=60 && scoure < 80)
			{
				//创建（声明队列）
				channel.queueDeclare("direct-2", false, false, false, null);
				//队列与交换机绑定
				channel.queueBind("direct-2", EXCHANGE_NAME, routingKey);
				channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
			}
			else if(scoure >=80 && scoure < 90)
			{
				//创建（声明队列）
				channel.queueDeclare("direct-3", false, false, false, null);
				//队列与交换机绑定
				channel.queueBind("direct-3", EXCHANGE_NAME, routingKey);
				channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
			}
			else
			{
				//创建（声明队列）
				channel.queueDeclare("direct-4", false, false, false, null);
				//队列与交换机绑定
				channel.queueBind("direct-4", EXCHANGE_NAME, routingKey);
				channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
			}*/
			System.out.println("[routingKey = "+routingKey+"] Sent msg is '" + message + "'");
 
			//Thread.sleep(1000 * 1000);
			channel.close();
			connection.close();
			
		}
		
	}
 
 
}
