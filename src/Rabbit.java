import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;

import java.util.UUID;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class Rabbit 
{
//	private final static String QUEUE_NAME = "broker_amqp_actmq";
/*	
	public static void test ()
	{
		try {
			String msg = "123321";
//			SendMessage (msg);
			msg = ReceiveMessage();
			System.out.println("rec: " + msg);
		} catch(Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
	}
*/
	
	public static ConnectionFactory GetConnection() throws Exception
	{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(Core.config_.rabbitHost_);
		factory.setPort(Core.config_.rabbitPort_);
		factory.setUsername(Core.config_.rabbitUser_);
		factory.setPassword(Core.config_.rabbitPwd_);
		factory.setConnectionTimeout(Core.config_.rabbitConnTimeoutSek_);

//		factory.setHost("10.250.9.7");
//		factory.setPort(5672);
//		factory.setUsername("guest");
//		factory.setPassword("guest");
//		factory.setConnectionTimeout(3000);
		
		return factory;
	}
	
	public static void SendMessage (String msg) throws Exception
	{
		ConnectionFactory factory = GetConnection();
		
		try (Connection connection = factory.newConnection();
		     Channel channel = connection.createChannel()) 
		{
			channel.queueDeclare(Core.config_.rabbitQueueName_, true, false, false, null);
			channel.basicPublish("", Core.config_.rabbitQueueName_, null, msg.getBytes());
			Core.log_.info("RbSent '" + msg + "'");
		}
	}

	public static void ReceiveMessage () 
	{
		Core.log_.info("RbRecv start");
		try {
			ConnectionFactory factory = GetConnection ();
			Connection connection = factory.newConnection ();
		    Channel channel = connection.createChannel ();
	
			channel.queueDeclare (ConfigLocal.rabbitQueueName_, true, false, false, null);
			Core.log_.info("RbRecv waiting for msg");
	
		    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
		        String message = new String(delivery.getBody(), "UTF-8");
	//	        saMsgRet.append(message);
//				Core.log_.info("RbRecv msg '" + message + "'");
				try {
		    		String sFN = "rabb_" + UUID.randomUUID().toString() + ".xml";
					String sPF = Core.GetSavedDocPF (ConfigLocal.dirInboxFromRabb_, sFN, message); 	
					Core.log_.info("RbRecv PF " + sPF);
				} catch(Exception e)
				{
					Core.log_.error("RbRecv error rcv " + e.toString());
				}
		    };
		    channel.basicConsume (ConfigLocal.rabbitQueueName_, true, deliverCallback, consumerTag -> { });
		} catch(Exception e)
		{
			Core.log_.error("RbRecv error main " + e.toString());
		}
		Core.log_.info("RbRecv finish");
	}
}
