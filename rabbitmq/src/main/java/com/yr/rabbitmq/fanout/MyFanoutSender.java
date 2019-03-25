package com.yr.rabbitmq.fanout;
import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
 /**
  * 
  * @author Administrator
  * @since
  * 扇形交换机会把能接收到的消息全部发送给绑定在自己身上的队列
  */
public class MyFanoutSender {
	
    //只有先绑定在交换机上的队列才能进行数据消费.
	
    private static final String MESSAGE = "my name is";
    private static final String EXCHANGE_NAME = "exchange_fanout";
    
    public static void main(String[] args){
    	for(int i=0;i<100;i++){
    	Connection conn = null;
    	Channel channel = null;
    	try {
    		//初始化连接，主机，端口，用户名，密码可以自己定义
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
			//创建连接
			conn = factory.newConnection(address);
			//创建通道
			channel = conn.createChannel();
			//定义为fanout类型的交换机
			channel.exchangeDeclare(EXCHANGE_NAME, "fanout", false, false, null);
			
			/*
			String queueName = "fanout-1";
			channel.queueDeclare(queueName, false, false, false, null);
			//队列queue与exchange绑定；
			channel.queueBind(queueName, EXCHANGE_NAME, "");
			*/
			
			/***
			               任何发送到Fanout Exchange的消息都会被转发到与该Exchange绑定(Binding)的所有Queue上。
			     1.可以理解为路由表的模式
                 2.这种模式不需要RouteKey
                 3.这种模式需要提前将Exchange与Queue进行绑定，一个Exchange可以绑定多个Queue，一个Queue可以同多个Exchange进行绑定。
                 4.如果接受到消息的Exchange没有与任何Queue绑定，则消息会被抛弃。
			 */
			for (int j = 1; j < 3; j++) {
				//声明/创建队列；
				String queueName = "fanout.logs.#";
				if(j%2 == 0)
				{
					queueName = "queue.fanout";
				}
				channel.queueDeclare(queueName, false, false, false, null);
				//队列queue与exchange绑定；
				channel.queueBind(queueName, EXCHANGE_NAME, "");
			}
			
			channel.basicPublish(EXCHANGE_NAME, "", null, MESSAGE.getBytes());
			System.out.println("I send a fanout massage!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(channel != null){
					channel.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    	}
    }
}
