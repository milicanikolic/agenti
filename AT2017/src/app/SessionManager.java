package app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.websocket.Session;


@Startup
@Singleton
public class SessionManager implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 7353141863157972412L;

	public SessionManager() {
		// TODO Auto-generated constructor stub
	}

	List<Session> sessions;
	
	@PostConstruct
	public void init()
	{

		sessions = new ArrayList<Session>();
	}

	public List<Session> getSessions() {
		return sessions;
	}

	public void setSessions(List<Session> sessions) {
		this.sessions = sessions;
	}
	public void addSession(Session s)
	{
		if(!sessions.contains(s))
			sessions.add(s);
	}
	public void removeSession(Session s)
	{
		sessions.remove(s);
	}
}