package rest;

import java.util.Collection;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import app.AgentManager;
import model.AbstractAgent;

@LocalBean
@Path("/agent")
@Stateless
public class AgentImpl implements AgentInterface{
	
	AgentManager am;
	
	@GET
	@Path("/running")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<AbstractAgent> getRunning() {
		return am.getRunning().values();
		
	}
	
	@GET
	@Path("/existing")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getExisting(){
		return am.getExisting();
	}
}
