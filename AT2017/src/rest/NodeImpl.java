package rest;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.websocket.Session;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import model.Agent;
import model.AgentCenter;

@LocalBean
@Path("/node")
@Stateless

public class NodeImpl implements NodeInterface {

	@EJB
	NodeManager nodes;

	@EJB
	SessionManager sm;
	
	@EJB
	AgentManager am;

	
	@GET
	 @Produces(MediaType.APPLICATION_JSON)
	
	 @Path("/registerNode/{address}/{port}/{alias}")
	 public List<AgentCenter> register(@PathParam("address") String address, @PathParam("port") String port, @PathParam("alias") String alias){
	  
	  System.out.println("usao u registar u masteru, registruje se: "+port);
	  if (!(nodes.getCurrent().getAddress().equals(address)) || !(nodes.getCurrent().getAlias().equals(alias))
	       || !(nodes.getCurrent().getPort().equals(port))) {
	      nodes.getNodes().add(new AgentCenter(address, port, alias));

	      for (int i=0; i<nodes.getNodes().size()-1;i++) {

	       System.out.println("gadja update na: "+nodes.getNodes().get(i).getAddress()+":"+nodes.getNodes().get(i).getPort());
	       ResteasyClient client = new ResteasyClientBuilder().build();
	       ResteasyWebTarget target = client
	         .target("http://" + UtilMethods.getLocalAddress() +":"+ nodes.getNodes().get(i).getPort() + "/AT2017/rest/node/updateNodes");
	          
	       Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(nodes.getNodes(), "application/json"));
	       

	      }
	    }
	  System.out.println("pre return lista size: "+nodes.getNodes().size());
	     return(List<AgentCenter>) nodes.getNodes();
	  
	 }

	
	@POST
	@Path("/updateSession")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateSession(List<Session> newSessions){
		for(Session s:newSessions) {
				if(!(sm.getSessions().contains(s))){
					sm.addSession(s);
			}
		}
	}
	
	@GET
	@Path("/isAlive")
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean isAlive(){
		return true;
	}
	
	
	
	

	@POST
	@Path("/updateNodes")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateNodes(List<AgentCenter> newNodes){
		
		ArrayList<AgentCenter> newList = (ArrayList<AgentCenter>) newNodes;
		nodes.setNodes(newList);
		System.out.println("ima " + nodes.getNodes().size() + " cvorova");

	}

	@PUT
	@Path("/unregisterNode")
	@Consumes(MediaType.APPLICATION_JSON)
	public void unregister(AgentCenter host) {
		System.out.println("usao u unregister");
		for (int i = 0; i < nodes.getNodes().size(); i++) {
			AgentCenter tren = nodes.getNodes().get(i);
			if (tren.getAddress().equals(host.getAddress()) && tren.getAlias().equals(host.getAlias()) && tren.getPort().equals(host.getPort())) {
				List<String> toDelete = new ArrayList<String>();
 				for(Agent a : am.getRunning().values()){
					if(a.getAid().getHost().getPort().equals(tren.getPort())){
						toDelete.add(a.getAid().getName());
					}
				}
					ResteasyClient client1 = new ResteasyClientBuilder().build();
					ResteasyWebTarget target1 = client1.target("http://" + UtilMethods.getLocalAddress() + ":" + tren.getPort() + "/AT2017/rest/agent/updateAgent");
					Response response1 = target1.request(MediaType.APPLICATION_JSON).post(Entity.entity(toDelete,MediaType.APPLICATION_JSON));
				
				System.out.println(
						"izbrisao:" + nodes.getNodes().get(i).getAddress() + " " + nodes.getNodes().get(i).getPort());
				nodes.getNodes().remove(i);

				break;
			}
		}

		for (AgentCenter h : nodes.getNodes()) {
			System.out.println("salje update na: " + h.getAddress() + " " + h.getPort());
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client
					.target("http://" + h.getAddress() + ":" + h.getPort() + "/AT2017/rest/node/updateNodes");

			Response response = target.request().post(Entity.entity(nodes.getNodes(), "application/json"));

			String ret = response.readEntity(String.class);

		}
	}

	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
}
