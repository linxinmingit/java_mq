package com.yr.rabbitmq.fanout;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class MyFirstFanoutReceiver {

	private static final String EXCHANGE_NAME = "exchange_fanout";

	public static void main(String[] args) {
		Connection conn = null;
		Channel channel = null;
		try {
			// 初始化连接
			ConnectionFactory factory = new ConnectionFactory();
			Address address[] = new Address[3];
			Address addr1 = new Address("192.168.1.181");
			Address addr2 = new Address("192.168.1.182");
			Address addr3 = new Address("192.168.1.183");
			address[0] = addr1;
			address[1] = addr2;
			address[2] = addr3;

			factory.setPort(5672);
			factory.setUsername("test");
			factory.setPassword("test");
			// 创建连接
			conn = factory.newConnection(address);
			// 创建通道
			channel = conn.createChannel();
			// 声明交换机类型
			channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT,false,false,null);

			// 声明默认的队列
			// String queue = channel.queueDeclare().getQueue();
			String queue = "queue.fanout";
			// 将队列与交换机绑定，最后一个参数为routingKey,与发送者指定的一样""
			channel.queueDeclare(queue, false, false, false, null);//
			channel.queueBind(queue, EXCHANGE_NAME, "", null); // routingKey设置为空
			// 消费者
			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
						byte[] body) throws IOException {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("MyFirstFanoutReceiver---->>"+new String(body, "utf-8"));
				}

			};
			channel.basicConsume(queue, true, consumer);
			System.out.println("i am the first fanout receiver!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
