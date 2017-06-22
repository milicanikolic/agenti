package agents;

import java.io.File;
import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.naming.NamingException;
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
import enums.AgentTypeEnum;
import enums.Performative;
import mdb.MDBProducer;
import model.ACLMessage;
import model.AID;
import model.AbstractAgent;
import model.Agent;
import model.AgentCenter;
import model.AgentType;

@Stateful
@Remote(Agent.class)
public class WordCounter extends AbstractAgent {
	
	@EJB
	private AgentManager am;
	@EJB
	private SessionManager sm;
	@EJB
	NodeManager nm;
	private int numberOfSlaves=0;
	private AID originSender;
	private int totalWordCount=0;
	private File[] files;
	

	@Override
	protected void onMessage(ACLMessage message) {
		if(message.getPerformative().equals(Performative.REQUEST)){
			numberOfSlaves = 0;
			totalWordCount = 0;
			System.out.println("USAO U WC");
			originSender = message.getSender();
			String path = message.getContent();
			File folder = new File(path);
			files = folder.listFiles();
			 
			for(int i=0; i<files.length; i++){
				if(files[i].isFile()){
					String name = "Slave"+i;
					System.out.println("SLAVE " + name);
					AgentCenter ac=new AgentCenter(UtilMethods.getCurrentAddress(), UtilMethods.getPort(), UtilMethods.getCurrentName());
					AID aid = new AID(name,ac,new AgentType(AgentTypeEnum.WordCounter.toString()));
					//WordCounterSlave wcs = new WordCounterSlave(aid,MDBProducer);
					numberOfSlaves++;
					ACLMessage aclMessage = new ACLMessage();
					aclMessage.setPerformative(Performative.REQUEST);
					aclMessage.setSender(message.getReplyTo());
					aclMessage.setReplyTo(aid);
					aclMessage.setContent(files[i].getAbsolutePath());
					
					
					
					try {
						Object o = nm.getCtx().lookup("java:global/AT2017/WordCounterSlave!model.Agent");
						if(o!=null) {
							
							System.out.println("nije null slave! ");
							Agent ag = (Agent)o;
							ag.init(aid);
							ag.handleMessage(aclMessage);
						}
						
					} catch (NamingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					
				}
			}
		}
		else if(message.getPerformative().equals(Performative.AGREE)){
			numberOfSlaves--;
			totalWordCount += Integer.parseInt(message.getContent());
			if(numberOfSlaves == 0){
				ACLMessage aclM = new ACLMessage();
				aclM.setPerformative(Performative.INFORM);
				aclM.setSender(message.getReplyTo());
				aclM.setReplyTo(originSender);
				aclM.setContent("WordCounter "+ originSender.getName() + " found totally " + totalWordCount + " words from " + files.length+" files");
			
				if(am.isOnSameNode(originSender,message.getReplyTo())){
					MDBProducer.sendJMS(aclM);
				}
				else{
					 ResteasyClient client = new ResteasyClientBuilder().build();
					   ResteasyWebTarget target = client.target(
							   "http://" + UtilMethods.getLocalAddress() + ":" + originSender.getHost().getPort() + "/AT2017/rest/agent/sendToOtherNode");
						  Response response = target.request().post(Entity.entity(aclM, "application/json"));
				}
			}
		}
		else if(message.getPerformative().equals(Performative.INFORM)){
		
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

}
