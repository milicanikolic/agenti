package rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import javax.websocket.Session;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
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
import model.Agent;
import model.AgentCenter;
import model.AgentType;

@LocalBean
@Path("/agent")
@Stateless
public class AgentImpl implements AgentInterface{
	
	@EJB
	SessionManager sm;
	
	@EJB 	
	AgentManager am;
	@EJB
	NodeManager nm;
	
	@GET
	@Path("/running")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getRunning() {
		System.out.println("USAO U RUNNING");
		List<String> listaStringAgenata = new ArrayList<String>();
		
		for(Agent aaa : am.getRunning().values()){
			System.out.println(aaa.getAid().getName());
			listaStringAgenata.add(aaa.getAid().getName());
		}
		return listaStringAgenata;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/existing")
	public List<String> getExisting(){
		System.out.println("Pogodio existing iz angulara");
		List<String> lista = am.getExisting();
		for(int i=0; i<lista.size(); i++) {
			System.out.println("agent: " + lista.get(i));
		}
		return am.getExisting();
	}
	
	
	@POST
	@Path("/send/{performative}/{sender}/{replyTo}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void send(@PathParam("performative") String performative, @PathParam("sender") String sender,
			@PathParam("replyTo") String replyTo,
			 String content){
		System.out.println("usao sam u SEND metodu");
		ACLMessage aclMessage = new ACLMessage();
		aclMessage.setPerformative(Performative.valueOf(performative));
		aclMessage.setSender(am.getAgentByName(sender));
		aclMessage.setReplyTo(am.getAgentByName(replyTo));
		System.out.println(am.getAgentByName(replyTo).getHost().getPort() + am.getAgentByName(sender).getHost().getPort());
		aclMessage.setContent(content);
			
		if(am.isOnSameNode(am.getAgentByName(sender),(am.getAgentByName(replyTo)))){
			MDBProducer.sendJMS(aclMessage);
		}
		else{
			System.out.println("usao ya slanje na jms");
			 ResteasyClient client = new ResteasyClientBuilder().build();
			   ResteasyWebTarget target = client.target(
					   "http://" + UtilMethods.getLocalAddress() + ":" + am.getAgentByName(replyTo).getHost().getPort() + "/AT2017/rest/agent/sendToOtherNode");
				  Response response = target.request().post(Entity.entity(aclMessage, "application/json"));
		}
	}
	
	@POST
	@Path("/sendToOtherNode")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendToOtherNode(ACLMessage aclMessage){
		System.out.println("usao u other node");
		MDBProducer.sendJMS(aclMessage);
	}
	
	@POST
	@Path("/updateAgent")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateAgent(List<String> list){
		for(String s : list){
			if(am.getRunning().containsKey(s)){
				am.getRunning().remove(s);
			}
		}
	}
	
	
	@GET
	@Path("/startAgent/{name}/{type}/{port}")
	@Produces(MediaType.APPLICATION_JSON)
	public AID start(@PathParam("name") String name,@PathParam("type") String type, @PathParam("port") String port) {
		System.out.println("usao u startagent: " + name + " "+ type);
		boolean exist=false;
		
		if(am.getRunning().containsKey(name)) {
			exist=true;
		}
		
		
		if(!exist){
		AgentCenter ac=new AgentCenter(UtilMethods.getCurrentAddress(), port, UtilMethods.getCurrentName());
			
		AgentType at=new AgentType(type);
		
		AID aid=new AID(name,ac, at);
		
		try {
		
			Object o=null;
			System.out.println("kontekst je " + nm.getCtx());
			if(at.getName().equals(AgentTypeEnum.Ping.toString())) {
				o = nm.getCtx().lookup("java:global/AT2017/Ping!model.Agent");
			}
			if(at.getName().equals(AgentTypeEnum.Pong.toString())) {
				o = nm.getCtx().lookup("java:global/AT2017/Pong!model.Agent");
			}
			if(at.getName().equals(AgentTypeEnum.WordCounter.toString())) {
				o = nm.getCtx().lookup("java:global/AT2017/WordCounter!model.Agent");
			}
			if(at.getName().equals(AgentTypeEnum.CNetInitiator.toString())) {
				o = nm.getCtx().lookup("java:global/AT2017/CNetInitiator!model.Agent");
			}
			if(at.getName().equals(AgentTypeEnum.CNetParticipant.toString())) {
				o = nm.getCtx().lookup("java:global/AT2017/CNetParticipant!model.Agent");
			}
			
			if(o!=null) {
				
				System.out.println("nije null ");
				Agent ag = (Agent)o;
				ag.init(aid);
				am.getRunning().put(aid.getName(), ag);
				
				String msg=type+ " " +name + " created.";
				System.out.println("nesto " + ag.getAid().getName());
				for(Session s : sm.getSessions()){
					try {
						s.getBasicRemote().sendText(msg);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
				for(AgentCenter agc: nm.getNodes()) {
					System.out.println("gadja update na: " + agc.getPort());
					 ResteasyClient client1 = new ResteasyClientBuilder().build();
					   ResteasyWebTarget target1 = client1.target(
							   "http://" + UtilMethods.getLocalAddress() + ":" + agc.getPort() + "/AT2017/rest/agent/startAgent/"+name+"/"+type + "/" +port);
					   Response response = target1.request().get();
					
				}
				return aid;
				
			
		} }catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
return null;
			
		}
	

	
	

}
