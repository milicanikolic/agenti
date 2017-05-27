package app;

import java.io.Serializable;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
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

	public NodeManager() {
		
	}

	
	@PostConstruct
	public void init() {
		nodes = new ArrayList<AgentCenter>();
		current = new AgentCenter(UtilMethods.getCurrentAddress(), UtilMethods.getPort(), UtilMethods.getMasterAlias());
		master = new AgentCenter(UtilMethods.getMasterAddress(), UtilMethods.getMasterPort(), UtilMethods.getMasterAlias());

		nodes.add(master);
		System.out.println("MASTER " + master.getPort());
		System.out.println("TRENUTNI " +  current.getPort());
		if (!master.getPort().equals(current.getPort())) {
			ResteasyClient client = new ResteasyClientBuilder().build();
			System.out.println("razlicit od mastera, registruje se");
			ResteasyWebTarget target = client
					.target("http://" + master.getAddress() + ":" + master.getPort() + "/AT2017/rest/node/registerNode/"+current.getAddress()+"/"+ current.getPort()+"/"+current.getAlias());

			Response response = target.request(MediaType.APPLICATION_JSON).get();

			String ret = response.readEntity(String.class);

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
/*
	@PreDestroy
	public void unregister() {

		if (!master.getPort().equals(current.getPort())) {
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget target = client.target(
					"http://" + master.getAddress() + ":" + master.getPort() + "/AT2017/rest/node/unregisterNode");
			
			Response response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(current, "application/json"));
		}
	}*/

	
	
	
	/*	@PostConstruct
	public void start() {

		System.out.println("usao u post construct");
		currentNode = new AgentCenter(UtilMethods.getCurrentAddress(), UtilMethods.getCurrentName(), UtilMethods.getPort());
		masterNode = new AgentCenter(UtilMethods.getMasterAddress(), UtilMethods.getMasterAlias(), UtilMethods.getMasterPort());
		System.out.println("MASTER " + masterNode.getAddress()+masterNode.getAlias()+masterNode.getPort() );
		System.out.println("TRENUTNI " + currentNode.getAddress()+currentNode.getAlias()+currentNode.getPort());
		
		allNodes.add(masterNode);

		if (!masterNode.getPort().equals(currentNode.getPort())) {
			ResteasyClient client = new ResteasyClientBuilder().build();
			System.out.println("razliciti su i usao je u if");
			ResteasyWebTarget target = client
					.target("http://"+ masterNode.getAddress() + ":" + masterNode.getPort() + "/AT2017/rest/node/register");

			Response response = target.request().post(Entity.entity(currentNode, "application/json"));
			//String ret = response.readEntity(String.class);
			System.out.println("vratio iz registera  " + response.getStatus());
			
			if(response.getStatus() >=200 && response.getStatus()<500) {
				ResteasyClient clientUpdate = new ResteasyClientBuilder().build();
				ResteasyWebTarget targetUpdate = clientUpdate
						.target("http://"+masterNode.getAddress() + ":" + masterNode.getPort() + "/AT2017/rest/node/updateMaster");
				System.out.println("restom gadjam update " + masterNode.getPort());
				Response responseUpdate = targetUpdate.request().get();
			}
			else {
				ResteasyClient client1 = new ResteasyClientBuilder().build();
				System.out.println("razliciti su i usao je u if");
				ResteasyWebTarget target1 = client1
						.target("http://"+ masterNode.getAddress() + ":" + masterNode.getPort() + "/AT2017/rest/node/register");

				Response response1 = target1.request().post(Entity.entity(currentNode, "application/json"));
				if(response1.getStatus() >=200 && response1.getStatus()<500) {
					ResteasyClient clientUpdate = new ResteasyClientBuilder().build();
					ResteasyWebTarget targetUpdate = clientUpdate
							.target("http://"+masterNode.getAddress() + ":" + masterNode.getPort() + "/AT2017/rest/node/updateMaster");
					System.out.println("restom gadjam update " + masterNode.getPort());
					Response responseUpdate = targetUpdate.request().get();
				}
				else {
					return;
				}
			}

			
		}
	}
	*/
	
	
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

}
