package mdb;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import app.AgentManager;
import model.ACLMessage;
import model.AID;
import model.Agent;

@Startup
@Singleton
public class MDBConsumer implements MessageListener {
	@EJB
	private AgentManager am;
	 private QueueReceiver queueReceiver;
	 private QueueSession sessionPublish;
	 public static final String q1="jms/queue/JMSqueue";
	 
	  private Context context;
	  private QueueConnectionFactory cf;
	  private Queue queue1;
	  private QueueConnection connection;

	 public MDBConsumer() throws NamingException {
	  try {
	   this.context= new InitialContext();
	      this.cf=(QueueConnectionFactory) context.lookup("java:jboss/exported/jms/RemoteConnectionFactory");
	      this.queue1=(Queue) context.lookup(q1);
	      
	      context.close();
	      this.connection=cf.createQueueConnection();
	      
	      this.sessionPublish=connection.createQueueSession(false,Session.AUTO_ACKNOWLEDGE);
	      this.connection.start();
	   
	   queueReceiver=sessionPublish.createReceiver(queue1);
	   this.queueReceiver.setMessageListener(this);
	  } catch (JMSException e) {
	   // TODO Auto-generated catch block
	   e.printStackTrace();
	  }
	  
	 }
	 
	 @Override
	 public void onMessage(Message arg0) {
		 System.out.println("on message u MSDB-uuuuuu");
		 ACLMessage message=null;
		try {
			message = (ACLMessage) ((ObjectMessage) arg0).getObject();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  AID replyTo = message.getReplyTo();
	
	  
	  Agent aAgent = am.getRunning().get(replyTo.getName());
	  aAgent.handleMessage(message);
	  
	  
	 }

}
