package app;

import java.io.Serializable;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.json.JSONArray;
import org.json.JSONException;

import model.AgentCenter;

@Startup
@Singleton
public class NodeManager implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 6166868604736895469L;
	private ArrayList<AgentCenter> nodes;
	private AgentCenter current;
	private AgentCenter master;
	private Context ctx=null;;

	public NodeManager() {
		
	}

	
	@PostConstruct
	public void init() {
		nodes = new ArrayList<AgentCenter>();
		current = new AgentCenter(UtilMethods.getCurrentAddress(), UtilMethods.getPort(), UtilMethods.getMasterAlias());
		master = new AgentCenter(UtilMethods.getMasterAddress(), UtilMethods.getMasterPort(), UtilMethods.getMasterAlias());

		nodes.add(master);
		
		try {
			ctx=new InitialContext();
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("MASTER " + master.getPort());
		System.out.println("TRENUTNI " +  current.getPort());
		if (!master.getPort().equals(current.getPort())) {
			ResteasyClient client = new ResteasyClientBuilder().build();
			System.out.println("razlicit od mastera, registruje se");
			ResteasyWebTarget target = client
					.target("http://"+ UtilMethods.getLocalAddress() + ":" + master.getPort() + "/AT2017/rest/node/registerNode/"+current.getAddress()+"/"+ current.getPort()+"/"+current.getAlias());

			Response response = target.request(MediaType.APPLICATION_JSON).get();

			String ret = response.readEntity(String.class);
			
			System.out.println(ret);

			ArrayList<AgentCenter> lista = new ArrayList<AgentCenter>();

			JSONArray jsonA;
			try {
				jsonA = new JSONArray(ret);

				System.out.println("u listi sada:");

				for (int i = 0; i < jsonA.length(); i++) {
					AgentCenter newOne = new AgentCenter();
					newOne.setAddress(jsonA.getJSONObject(i).getString("address"));
					newOne.setAlias(jsonA.getJSONObject(i).getString("alias"));
					newOne.setPort(jsonA.getJSONObject(i).getString("port"));
					System.out.println(newOne.getAddress() + " " + newOne.getPort());
					lista.add(newOne);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			setNodes(lista);

		}
	}
	
	public void deleteByAlias(String alias){
		for(AgentCenter ac : nodes){
			if(ac.getAlias().equals(alias)){
				
				nodes.remove(ac);
				break;
			}
		}
	}

	@PreDestroy
	public void unregister() {

		if (!master.getPort().equals(current.getPort())) {
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target(
					"http://" + master.getAddress() + ":" + master.getPort() + "/AT2017/rest/node/unregisterNode");
			
			Response response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(current, "application/json"));
		}
	}

	
	public ArrayList<AgentCenter> getNodes() {
		return nodes;
	}

	public void setNodes(ArrayList<AgentCenter> nodes) {
		this.nodes = nodes;
	}

	public AgentCenter getCurrent() {
		return current;
	}

	public void setCurrent(AgentCenter current) {
		this.current = current;
	}

	public AgentCenter getMaster() {
		return master;
	}

	public void setMaster(AgentCenter master) {
		this.master = master;
	}


	public Context getCtx() {
		return ctx;
	}


	public void setCtx(Context ctx) {
		this.ctx = ctx;
	}
	
	

}
