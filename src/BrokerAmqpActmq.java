import java.util.Date;
/*
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
*/
public class BrokerAmqpActmq {

    public static void main(String[] args) 
    {
		try {
        	Core.initialize();
        	Core.Run();
		} catch(Exception e) {
			e.printStackTrace();
		}
//    	Rabbit.test();
//    	ActiveMQ.readMq();
//    	readMq();
    	
    }
    
/*
    public static void readMq()
    {
        com.ibm.mq.jms.MQQueueConnectionFactory factory = new MQQueueConnectionFactory();
        //factory.setHostName(host);
        //factory.setPort(port);
        factory.setConnectionNameList(hosts);
        factory.setQueueManager(qmgrName);
        factory.setChannel(channelName);
        factory.setTransportType(WMQConstants.WMQ_CM_CLIENT); // TRANSPORT_MQSERIES_CLIENT
        factory.setCCSID(1208);
        factory.setClientReconnectTimeout(600);
        factory.setClientReconnectOptions(WMQConstants.WMQ_CLIENT_RECONNECT);
        
        try {

        Connection connection = factory.createConnection(login, password);
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(queueName);
        MessageProducer producer = session.createProducer(queue);
        
        TextMessage message;
        for(int i=0; i < 10000; i++) {
	        message = session.createTextMessage("Hellos");
	        producer.send(message);
	        System.out.println("SimpleStandAloneSender.main() " + i + " " +  message.getJMSMessageID() +  " " + new Date());
	        //Thread.sleep(1000);
        }
        
        producer.close();
        session.close();
        connection.close();
        } catch(Exception e){
        	String str = e.toString();
        }
    }
*/
}
