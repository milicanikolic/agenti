package app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import model.AID;
import model.Agent;

@Startup
@Singleton
public class AgentManager implements Serializable {
	
	private static final long serialVersionUID = -7072733263569660868L;
	private List<String> existing; 
	private HashMap<String, Agent> running;
	
	
	public AgentManager() {
				
	}
	
	@PostConstruct
	public void init() {
		running=new HashMap<String, Agent>();
		existing=new ArrayList<String>(UtilMethods.getAgentNames());
	}
	
	public AID getAgentByName(String name){
		return running.get(name).getAid();
	}

	public boolean isOnSameNode(AID first, AID second ){
	if((first.getHost().getAddress().equals(second.getHost().getAddress())) && (first.getHost().getAlias().equals(second.getHost().getAlias())) 
			&& (first.getHost().getPort().equals(second.getHost().getPort()))) {
		return true;
		
	}
	else
		return false;
	}
	
	public List<String> getExisting() {
		return existing;
	}
	public void setExisting(List<String> existing) {
		this.existing = existing;
	}

	public HashMap<String, Agent> getRunning() {
		return running;
	}

	public void setRunning(HashMap<String, Agent> running) {
		this.running = running;
	}
	
	
	

}
