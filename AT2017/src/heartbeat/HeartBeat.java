package heartbeat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import app.AgentManager;
import app.NodeManager;
import app.UtilMethods;
import model.Agent;
import model.AgentCenter;

@Singleton
public class HeartBeat {

	@EJB
    private NodeManager nm;

	@EJB
	private AgentManager am;
	
    @Schedule(hour = "*", minute = "*", second = "*/15", info = "every 45th second")
    public void isAlive() {
    	
        System.out.println("PROVJERAVAMO");
        
        for(AgentCenter ac : nm.getNodes()){
        	if(!(ac.equals(nm.getCurrent()))){
        		if(!(callResteasyClient(ac.getPort()))){
        			if(!(callResteasyClient(ac.getPort()))){
        				List<String> toDelete = new ArrayList<String>();
        				for(Agent a : am.getRunning().values()){
        					if(a.getAid().getHost().getPort().equals(ac.getPort())){
        						toDelete.add(a.getAid().getName());
        					}
        				}
        					ResteasyClient client1 = new ResteasyClientBuilder().build();
        					ResteasyWebTarget target1 = client1.target("http://" + UtilMethods.getLocalAddress() + ":" + ac.getPort() + "/AT2017/rest/agent/updateAgent");
        					Response response1 = target1.request(MediaType.APPLICATION_JSON).post(Entity.entity(toDelete,MediaType.APPLICATION_JSON));
        					nm.deleteByAlias(ac.getAlias());
        					ResteasyClient client = new ResteasyClientBuilder().build();
        			    	ResteasyWebTarget target = client.target("http://" + UtilMethods.getLocalAddress() + ":" + ac.getPort() + "/AT2017/rest/node/updateNodes");
        			    	Response response=target.request(MediaType.APPLICATION_JSON).post(Entity.entity(nm.getNodes(),MediaType.APPLICATION_JSON));
        					
        			}
        		}
        	}
        }
    }
    
    
    
    	public Boolean callResteasyClient(String port){
	    	try{
		    	ResteasyClient client = new ResteasyClientBuilder().establishConnectionTimeout(4, TimeUnit.SECONDS).socketTimeout(4, TimeUnit.SECONDS).build();
		    	ResteasyWebTarget target = client.target("http://" + UtilMethods.getLocalAddress() + ":" + port + "/AT2017/rest/node/isAlive");
		    	Response response=target.request(MediaType.APPLICATION_JSON).get();
		    	if(response.getStatus() >= 200 && response.getStatus()<500){
		    	return true;
		    	}
		    	else
		    	{
		    		return false;
		    	}
	    	}
	    	catch (Exception e) {
	    		e.printStackTrace();
	    		return false;
			}
    	}

}
