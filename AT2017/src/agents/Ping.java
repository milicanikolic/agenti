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
import app.NodeManager;
import app.SessionManager;
import app.UtilMethods;
import enums.Performative;
import model.ACLMessage;
import model.AbstractAgent;
import model.Agent;


@Stateful
@Remote(Agent.class)
public class Ping extends AbstractAgent{

	@EJB
	private AgentManager am;
	@EJB
	private SessionManager sm;
	
	@EJB
	NodeManager nm;

	@Override
	protected void onMessage(ACLMessage message) {
		// TODO Auto-generated method stub
		System.out.println("USAO SAM U PINGA");
		if(message.getPerformative().equals(Performative.INFORM)){
			
				for(Session s : sm.getSessions()){
				try {
					s.getBasicRemote().sendText(message.getContent());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
				ACLMessage aclMessage = new ACLMessage();
				aclMessage.setPerformative(Performative.PROXY);
				aclMessage.setSender(message.getReplyTo());
				aclMessage.setReplyTo(message.getSender());
				aclMessage.setContent(message.getContent());
				if(!(am.isOnSameNode(message.getSender(), message.getReplyTo()))){

					 ResteasyClient client = new ResteasyClientBuilder().build();
					   ResteasyWebTarget target = client.target(
							   "http://" + UtilMethods.getLocalAddress() + ":" + aclMessage.getReplyTo().getHost().getPort() + "/AT2017/rest/agent/sendToOtherNode");
						  Response response = target.request().post(Entity.entity(aclMessage, "application/json"));
				}
			System.out.println("Poslati na web socket");
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
