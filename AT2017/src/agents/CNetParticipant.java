package agents;

import java.util.Random;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
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
public class CNetParticipant extends AbstractAgent {
	
	@EJB
	private AgentManager am;
	@EJB
	private SessionManager sm;

	

	@Override
	protected void onMessage(ACLMessage message) {
		
		// TODO Auto-generated method stub
		
		if(message.getPerformative().equals(Performative.CALL_FOR_PROPOSAL)) {
			System.out.println("USAO U CALL_FOR_PROPOSAL");
			Random r=new Random();
			int bid=r.nextInt(Integer.parseInt(message.getContent()));
			
			ACLMessage aclM=new ACLMessage();
			
			aclM.setSender(message.getReplyTo());
			aclM.setReplyTo(message.getSender());
			aclM.setContent(String.valueOf(bid));
		
			
			if(r.nextBoolean()) {
				aclM.setPerformative(Performative.PROPOSE);
			}
			else {
				aclM.setPerformative(Performative.REFUSE);
			}
			
			if (am.isOnSameNode(aclM.getReplyTo(),aclM.getSender())) {
				MDBProducer.sendJMS(aclM);
			} else {
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget target = client.target("http://" + UtilMethods.getLocalAddress() + ":" + aclM.getReplyTo().getHost().getPort()
						+ "/AT2017/rest/agent/sendToOtherNode");
				Response response = target.request().post(Entity.entity(aclM, "application/json"));
			}
		}
		else if(message.getPerformative().equals(Performative.ACCEPT_PROPOSAL)) {
			ACLMessage aclM=new ACLMessage();
			aclM.setPerformative(Performative.AGREE);
			aclM.setSender(message.getReplyTo());
			aclM.setReplyTo(message.getSender());
			aclM.setContent("Best offer "+message.getContent()+ " from agent " + message.getReplyTo().getName());
			
			if (am.isOnSameNode(aclM.getReplyTo(),aclM.getSender())) {
				MDBProducer.sendJMS(aclM);
			} else {
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget target = client.target("http://" + UtilMethods.getLocalAddress() + ":" + aclM.getReplyTo().getHost().getPort()
						+ "/AT2017/rest/agent/sendToOtherNode");
				Response response = target.request().post(Entity.entity(aclM, "application/json"));
			}
		}else if(message.getPerformative().equals(Performative.REJECT_PROPOSAL)) {
			
		}

	}

}
