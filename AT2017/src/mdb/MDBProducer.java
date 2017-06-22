package mdb;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import model.ACLMessage;


public class MDBProducer {
		
	public static void sendJMS(ACLMessage message){
		try{
			
		Context	context=new InitialContext();
		QueueConnectionFactory	cf=(QueueConnectionFactory)context.lookup("java:jboss/exported/jms/RemoteConnectionFactory");
		Queue	queue1=(Queue)context.lookup("jms/queue/JMSqueue");
			context.close();
			
		QueueConnection	connection=cf.createQueueConnection();
		QueueSession sessionPublish=connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			connection.start();
			
			
		QueueSender	queueSender=sessionPublish.createSender(queue1);
		ObjectMessage om=sessionPublish.createObjectMessage(message);
		System.out.println(message.getSender().getName() +" salje JMS-om "+message.getReplyTo().getName()+" performativa: "+message.getPerformative()+" sadrzaj:"+message.getContent());
		queueSender.send(om);
		queueSender.close();
		connection.close();
			
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
		
	}

		

}