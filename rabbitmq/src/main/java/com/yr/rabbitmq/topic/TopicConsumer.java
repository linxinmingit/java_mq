package com.yr.rabbitmq.topic;
 
import java.io.IOException;
import java.util.concurrent.TimeoutException;
 
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
 
 
public class TopicConsumer {
 
	private static final String EXCHANGE_NAME 	= "exchange_topic";
	public static void main(String[] argv) throws IOException, TimeoutException  {
		
		//new ExchangeTopic("logs.info");
		//new ExchangeTopic("logs.*");
		new ExchangeTopic("logs.topic.error");
	}
 
	static class ExchangeTopic{
		public  ExchangeTopic(final String routingKey) throws IOException, TimeoutException {
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
			channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC, false, false, null);
			//队列名称
			String queueName = routingKey + ".queue";
			//创建队列
			channel.queueDeclare(queueName, false, false, false, null);
			//把队列绑定到路由上
			//channel.queueBind(queueName, EXCHANGE_NAME, routingKey);
			channel.queueBind(queueName, EXCHANGE_NAME, "logs.info.#");
 
			System.out.println(" [routingKey = "+ routingKey +"] Waiting for msg....");
 
			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope,
						AMQP.BasicProperties properties, byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					
					System.out.println("[routingKey = "+ routingKey +"] Received msg is '" + message + "'");
				}
			};
			channel.basicConsume(queueName, true, consumer);
		}
 
	}
 
}
