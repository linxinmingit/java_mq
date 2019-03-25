package com.yr.rabbitmq.topic;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.security.auth.kerberos.KerberosKey;

public class ReceiveLogsTopic {
	private static final String EXCHANGE_NAME = "exchange_topic";
	private static final String[] bindingKeys = { "student.insert","user.delete", "person.query", "animals.update","queue.#" };
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
		ReceiveLogsTopic.method1();
		//ReceiveLogsTopic.method2();
	}

	public static void method1() throws Exception {
		channel.exchangeDeclare(EXCHANGE_NAME, "topic");
		String queueName = channel.queueDeclare().getQueue();
		//String queueName ="topic-1";

		if (bindingKeys.length < 1) {
			System.err.println("Usage: ReceiveLogsTopic [binding_key]...");
			System.exit(1);
		}

		// binding那些bindingKey
		// 可以单个单个设置，也可以批量设置
		for (String bindingKey : bindingKeys) {
			channel.queueBind(queueName, EXCHANGE_NAME, "queue.#");
		}

		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
			}
		};
		channel.basicConsume(queueName, true, consumer);
		channel.close();
		connection.close();
	}

	public static void method2() throws Exception{
		channel.exchangeDeclare(EXCHANGE_NAME, "topic");

		if (bindingKeys.length < 1) {
			System.err.println("Usage: ReceiveLogsTopic [binding_key]...");
			System.exit(1);
		}

		// binding那些bindingKey
		for (int j = 1; j < 3; j++) {
			String queueName = "topic-"+j;
			channel.queueBind(queueName, EXCHANGE_NAME, "user.delete");
			
			System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
				}
			};
			channel.basicConsume(queueName, true, consumer);
		}
		
		channel.close();
		connection.close();
	}

}
