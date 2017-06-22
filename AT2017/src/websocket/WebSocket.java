package websocket;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.websocket.CloseReason;
import javax.websocket.Extension;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.server.ServerEndpoint;
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
import model.AgentCenter;


@ServerEndpoint("/websocket")
@Singleton
public class WebSocket {

	static List<Session> sessions = new ArrayList<Session>();
	
	@EJB
	SessionManager sm;

	@EJB
	NodeManager nm;

	@EJB
	AgentManager am;

	@OnOpen
	public void open(Session session) {
		if (!sessions.contains(session)) {
			sessions.add(session);
			System.out.println("SESN MENADZER" + sm);
			System.out.println(nm.getNodes().size());
			System.out.println("ON OPEEEEEEEEEEEEEEEEEEEEEEEN" + sessions.size() + sm.getSessions().size());

			sm.addSession(session);
			/*for(AgentCenter agc: nm.getNodes()) {
				 ResteasyClient client1 = new ResteasyClientBuilder().build();
				   ResteasyWebTarget target1 = client1.target(
				     "http://" + UtilMethods.getLocalAddress() + ":" + nm.getCurrent().getPort() + "/AT2017/rest/node/updateSessions");
				   Response response = target1.request().post(Entity.entity(session, MediaType.APPLICATION_JSON));
				
			}*/
		}
		
	}

	@OnClose
	public void close(Session session) {
		sessions.remove(session);
		sm.removeSession(session);
	}

	@OnError
	public void onError(Throwable error, Session session) {
		System.out.println("USAO U ONEREOR " + error.getMessage());
		sessions.remove(session);
		sm.removeSession(session);
	}

	@OnMessage
	public void handleMessage(String message, Session session) {
		String[] receivedMessage = message.split(":");
		String operation = receivedMessage[0];
		//System.out.println("CAOOOOOOO MILICEEEEEEEEE" + operation + nm.getCurrent().getPort());
		if(operation.equals("StartAgent")){
			String name = receivedMessage[1];
			String type = receivedMessage[2];
			String port=receivedMessage[3];
			String messageToShow = type + " agent " + name + " created.";
			ResteasyClient client = new ResteasyClientBuilder().build();
			   ResteasyWebTarget target = client.target(
				 "http://" + UtilMethods.getLocalAddress() + ":" + nm.getCurrent().getPort() + "/AT2017/rest/agent/startAgent/" + name + "/" + type+"/"+port);
				  Response response = target.request().get();
				  String ret = response.readEntity(String.class);
				  
		}
		else if(operation.equals("SendMessage")) {
			System.out.println("u ws send");
			String performative = receivedMessage[1];
			String sender = receivedMessage[2];
			String replyTo = receivedMessage[3];
			String content = receivedMessage[4];
			System.out.println(am.getAgentByName(replyTo).getHost().getPort());
			 ResteasyClient client = new ResteasyClientBuilder().build();
			   ResteasyWebTarget target = client.target(
					   "http://" + UtilMethods.getLocalAddress() + ":" + am.getAgentByName(replyTo).getHost().getPort() + "/AT2017/rest/agent/send/"+performative+"/"+
			   sender+"/"+replyTo);
				  Response response = target.request().post(Entity.entity(content, javax.ws.rs.core.MediaType.APPLICATION_JSON));
		}
	
	}

}
