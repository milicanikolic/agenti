package app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import model.AbstractAgent;

@Startup
@Singleton
public class AgentManager implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7072733263569660868L;
	private List<String> existing; 
	private HashMap<String, AbstractAgent> running;
	
	
	public AgentManager() {
				
	}
	
	@PostConstruct
	public void init() {
		running=new HashMap<String, AbstractAgent>();
		existing=new ArrayList<String>(UtilMethods.getAgentNames());
	}
	
	public List<String> getExisting() {
		return existing;
	}
	public void setExisting(List<String> existing) {
		this.existing = existing;
	}

	public HashMap<String, AbstractAgent> getRunning() {
		return running;
	}

	public void setRunning(HashMap<String, AbstractAgent> running) {
		this.running = running;
	}
	
	
	

}
