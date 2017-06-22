package agents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import enums.AgentTypeEnum;
import enums.Performative;
import mdb.MDBProducer;
import model.ACLMessage;
import model.AID;
import model.AbstractAgent;
import model.Agent;

@Stateful
@Remote(Agent.class)
public class CNetInitiator extends AbstractAgent {

	@EJB
	private AgentManager am;
	@EJB
	private SessionManager sm;
	private List<Agent> abstractAgents = new ArrayList<Agent>();
	private int numberOfParticipants=0;
	private HashMap<AID, Integer> accepted = new HashMap<AID, Integer>();
	private AID bestProposal;
	private AID originSender;

	
	@Override
	protected void onMessage(ACLMessage message) {
		if (message.getPerformative().equals(Performative.REQUEST)) {
			
			originSender=message.getSender();
			for (Agent aa : am.getRunning().values()) {
				System.out.println(aa.getAid().getType().getName());
				if (aa.getAid().getType().getName().equals(AgentTypeEnum.CNetParticipant.toString())) {
					abstractAgents.add(aa);
					System.out.println("PARTICIPANT" + aa.getAid().getName());
					numberOfParticipants++;
				}
			}
			for (Agent a : abstractAgents) {
				ACLMessage aclMessage = new ACLMessage();
				aclMessage.setPerformative(Performative.CALL_FOR_PROPOSAL);
				aclMessage.setSender(message.getReplyTo());
				aclMessage.setReplyTo(a.getAid());
				aclMessage.setContent(message.getContent());
				
				if (am.isOnSameNode(message.getReplyTo(), a.getAid())) {
					System.out.println("salje INITIATOR CALL...");
					MDBProducer.sendJMS(aclMessage);
				} else {
					ResteasyClient client = new ResteasyClientBuilder().build();
					ResteasyWebTarget target = client.target("http://" + UtilMethods.getLocalAddress() + ":" + a.getAid().getHost().getPort()
							+ "/AT2017/rest/agent/sendToOtherNode");
					Response response = target.request().post(Entity.entity(aclMessage, "application/json"));
				}
			}

		} else if (message.getPerformative().equals(Performative.REFUSE)) {
			numberOfParticipants--;
			if (numberOfParticipants == 0) {
				chooseTheBestProposal(message);
			}
		} else if (message.getPerformative().equals(Performative.PROPOSE)) {
			numberOfParticipants--;
			accepted.put(message.getSender(), Integer.parseInt(message.getContent()));
			if (numberOfParticipants == 0) {
				chooseTheBestProposal(message);
			}

		} 
		
		
		else if (message.getPerformative().equals(Performative.AGREE)) {
			ACLMessage aclMessage = new ACLMessage();
			aclMessage.setPerformative(Performative.INFORM);
			aclMessage.setSender(message.getReplyTo());
			aclMessage.setReplyTo(originSender);
			aclMessage.setContent(message.getContent());
			
			
			if (am.isOnSameNode(aclMessage.getReplyTo(),aclMessage.getSender())) {
				System.out.println("salje INITIATOR CALL...");
				MDBProducer.sendJMS(aclMessage);
			} else {
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget target = client.target("http://" + UtilMethods.getLocalAddress() + ":" + originSender.getHost().getPort()
						+ "/AT2017/rest/agent/sendToOtherNode");
				Response response = target.request().post(Entity.entity(aclMessage, "application/json"));
			}
		}
		else if (message.getPerformative().equals(Performative.INFORM)) {
			
				for (Session s : sm.getSessions()) {
					try {
						s.getBasicRemote().sendText(message.getContent());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
				

			} 
		}else if(message.getPerformative().equals(Performative.PROXY)){
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

	public void chooseTheBestProposal(ACLMessage aclMessage) {
		int minValue = Integer.MAX_VALUE;
		for (int number : accepted.values()) {
			if (number < minValue) {
				minValue = number;
			}
		}
		for (AID aid : accepted.keySet()) {
			if (accepted.get(aid) == minValue) {
				bestProposal = aid;
				break;
			}
		}
		System.out.println("najbolji agent" + bestProposal.getName() + minValue);
		
		
		 System.out.println(aclMessage.getPerformative().toString());
		
		 	
		 	for(AID a:accepted.keySet()){
			ACLMessage aclM=new ACLMessage();
			aclM.setSender(aclMessage.getReplyTo());
			aclM.setReplyTo(a);
			aclM.setContent(String.valueOf(minValue));
			
			if(a.getName().equals(bestProposal.getName())) {
				System.out.println("PRIHVATIO GAAAAA");
				aclM.setPerformative(Performative.ACCEPT_PROPOSAL);
			}else {
				aclM.setPerformative(Performative.REJECT_PROPOSAL);
				
			}
			
			
			if (am.isOnSameNode(aclMessage.getReplyTo(),a)) {
				MDBProducer.sendJMS(aclM);
			} else {
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget target = client.target("http://" + UtilMethods.getLocalAddress() + ":" + a.getHost().getPort()
						+ "/AT2017/rest/agent/sendToOtherNode");
				Response response = target.request().post(Entity.entity(aclM, "application/json"));
			}
			
		}
		}

}
