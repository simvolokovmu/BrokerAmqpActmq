import java.io.File;
import java.util.Set;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

public class ActiveMQ 
{
	public static final String host = "tcp://37.230.240.41:61616";
	public static final String login = "elcuser";
	public static final String pwd = "elcpass";
	
    public static void readMq()
    {
    	try {
    		ActiveMQConnection connect = ActiveMQConnection.makeConnection(login, pwd, host);
    		connect.start();
    		Set<ActiveMQQueue> allque = connect.getDestinationSource().getQueues();
    		
    		for (ActiveMQQueue que : allque)
    		{
                System.out.println(que.getQueueName());
    		}
    		
    	} catch(Exception e) {
        	String str = e.toString();
            System.out.println(str);
    	}
    }

    public static void SendMessages ()
    {
    	Core.log_.info("ActMq send start");
		File folder = new File(ConfigLocal.dirInboxFromRabb_);
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles.length == 0)
		{
	    	Core.log_.info("ActMq msg list is empty");
	    	return;			
		}

		try {
			//created ConnectionFactory object for creating connection 
			ConnectionFactory factory = new ActiveMQConnectionFactory(ConfigLocal.amqpUser_, ConfigLocal.amqpPwd_, ConfigLocal.amqpHost_ + ":" + ConfigLocal.amqpPort_); // admin", "admin", "tcp://localhost:61617");
			//Establish the connection
			Connection connection = factory.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue(ConfigLocal.amqpQueueName_);
			//Added as a producer
			javax.jms.MessageProducer producer = session.createProducer(queue);
			
			for (File file : listOfFiles) 
			{
			    if (!file.isFile())
			    	continue;
			    
			    String sPF = file.getAbsolutePath(), sFN = file.getName();
			    String sMsg = null;
			    try {
			    	sMsg = Core.readFile (sPF);
	
				    if(sMsg == null || sMsg.isEmpty())
				    {
					    Core.moveFile(sPF, ConfigLocal.dirTroubleFromRabb_ + '/' + sFN);
				    	continue;
				    }
				    
				    Core.log_.info("ActMq send " + sFN);
					// Create and send the message
					TextMessage msg = session.createTextMessage();
					msg.setText(sMsg);
				    producer.send(msg);
				    
				    Core.log_.info("ActMq send success " + sFN);
				    Core.moveFile(sPF, ConfigLocal.dirStoreFromRabb_ + '/' + sFN);
				} catch (Exception e) {
				    Core.log_.error ("ActMq send file error " + e.toString());
				}		    
			}
			
	        producer.close();
	        session.close();
	        connection.close();

		} catch(Exception e) {
		    Core.log_.error ("ActMq main error " + e.toString());
		}
    	Core.log_.info("ActMq send final");
    }

    
/*    
    public static void receiveMq()
    {
    	try {
            com.ibm.mq.jms.MQQueueConnectionFactory factory = new MQQueueConnectionFactory();
            //factory.setHostName(host);
            //factory.setPort(port);
            factory.setConnectionNameList(Core.config_.qm_hosts_);
            factory.setQueueManager(Core.config_.qm_gr_name_);
            factory.setChannel(Core.config_.qm_ch_name_);
            factory.setTransportType(WMQConstants.WMQ_CM_CLIENT); // TRANSPORT_MQSERIES_CLIENT
            factory.setCCSID(1208);
            factory.setClientReconnectTimeout(600);
            factory.setClientReconnectOptions(WMQConstants.WMQ_CLIENT_RECONNECT);

	        Connection connection = factory.createConnection(Core.config_.qm_login_, Core.config_.qm_pwd_);
	        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	        Queue queue = session.createQueue(Core.config_.qm_name_);

//            MessageProducer producer = session.createProducer(queue);
    	    MessageConsumer consumer = session.createConsumer(queue);
    	    
    	    connection.start();             // !DON'T FORGET!
    	    while (true)
    	    {
    	    	TextMessage receivedMessage = (TextMessage) consumer.receive(Core.config_.qm_timeout_sek_ * 1000); // or receive(), or receiveNoWait
    	    	if (receivedMessage == null)
    	    		break;
    	    	
    	    	try {
    	    		String sFN = "zags_" + UUID.randomUUID().toString() + ".xml";
    	    		String sPF = Core.GetSavedDocPF(Core.config_.dirInbox_, sFN, receivedMessage.getText());
    	            String resultMessageBody = "Success save " + sFN + " [ " + receivedMessage.getText() + " ] ";
    	            Core.log_.info(resultMessageBody);
    	    	} catch (Exception e)
    	    	{
    	    		Core.log_.error(e);
    	    	}
    	    }
    	    
    	} catch(Exception e)
    	{
        	String str = e.toString();
            Core.log_.info(str);
    	}
    }
*/	

}
