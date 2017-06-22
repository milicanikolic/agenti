package agents;

import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.websocket.Session;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import app.AgentManager;
import app.SessionManager;
import app.UtilMethods;
import enums.Performative;
import mdb.MDBProducer;
import model.ACLMessage;
import model.AbstractAgent;
import model.Agent;


@Stateful
@Remote(Agent.class)
public class Pong extends AbstractAgent {
	
	@EJB
	AgentManager am;
	@EJB
	SessionManager sm;
	

	@Override	
	protected void onMessage(ACLMessage message) {
		// TODO Auto-generated method stub
		System.out.println("USAO SAM U PONGA");
		if(message.getPerformative().equals(Performative.REQUEST)){
			System.out.println("PONG javlja PINGU");
			ACLMessage returnMessage = new ACLMessage();
			returnMessage.setPerformative(Performative.INFORM);
			returnMessage.setSender(message.getReplyTo());
			returnMessage.setReplyTo(message.getSender());
			returnMessage.setContent("PONG " + message.getReplyTo().getName() + " received message from PING " + message.getSender().getName() + " and replied back");
	
			if(am.isOnSameNode(returnMessage.getSender(), returnMessage.getReplyTo())){
				MDBProducer.sendJMS(returnMessage);
			}else
			{
				 ResteasyClient client = new ResteasyClientBuilder().build();
				   ResteasyWebTarget target = client.target(
						   "http://" + UtilMethods.getLocalAddress() + ":" + returnMessage.getReplyTo().getHost().getPort() + "/AT2017/rest/agent/sendToOtherNode");
					  Response response = target.request().post(Entity.entity(returnMessage, "application/json"));
			}
		}
		else if(message.getPerformative().equals(Performative.PROXY)){
			for(Session s : sm.getSessions()){
				try {
					s.getBasicRemote().sendText(message.getContent());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	

	}


}
