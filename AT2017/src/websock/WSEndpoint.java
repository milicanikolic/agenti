package websock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.json.JSONArray;
import org.json.JSONException;

import app.NodeManager;

@ServerEndpoint("/websocket")
public class WSEndpoint {
	
	
	Logger log = Logger.getLogger("Websockets endpoint");
	 List<Session> sessions = new ArrayList<Session>();

	 @EJB
	 NodeManager node;
	 
	 @OnOpen
	 public void open(Session session) {
	  if (!sessions.contains(session)) {
	   sessions.add(session);
	   System.out.println("ON OPEEEEEEEEEEEEEEEEEEEEEEEN");
	   log.info("Dodao sesiju: " + session.getId() + " u endpoint-u: " + this.hashCode() + ", ukupno sesija: " + sessions.size());
	  }
	 }

	 @OnClose
	 public void close(Session session) {
	 }

	 @OnError
	 public void onError(Throwable error) {
	  System.out.println("USAO U ONEREOR " + error.getMessage());
	 }

	 @OnMessage
	 public void handleMessage(String message, Session session) {
	 /* System.out.println("SALJEEEEEEEEEEEEEEEEEE");
	  System.out.println("DOSAO, poruka " + message);
	  
	  String poruka[] = message.split(";");
	  String tip = poruka[0];

	  if (tip.equals("Create")) {

	   String imeAgenta = poruka[1];
	   String tipAgenta = poruka[2];
	   String port = poruka[4];

	   String porukaZaJS = "Create;Agent " + imeAgenta + " kreiran.";

	   ResteasyClient client = new ResteasyClientBuilder().build();
	   ResteasyWebTarget target = client.target(
	     "http://localhost:" + port + "/Agents/rest/agent/startAgent/" + tipAgenta + "/" + imeAgenta);
	   Response response = target.request().get();
	   String ret = response.readEntity(String.class);
	   System.out.println("RET /**************" + ret);

	   // AKO JE REST BIO USPESAN, POSALJI PORUKU NA KLIJENTA
	   if (ret != null) {
	    
	    try {
	     log.info("Websocket endpoint: " + this.hashCode() + " primio: " + porukaZaJS + " u sesiji: "
	       + session.getId());
	     for (Session s : sessions) {

	      s.getBasicRemote().sendText(porukaZaJS);
	      log.info("Sending '" + porukaZaJS + "' to : " + s.getId());

	     }

	     
	    
	    
	    //OVO SADA DA APDEJTUJE ONE KOMBO BOKSOVE ZA PRIMA I SALJE 
	    ResteasyClient client1 = new ResteasyClientBuilder().build();
	    ResteasyWebTarget target1 = client.target(
	      "http://localhost:" + port + "/Agents/rest/agent/runningAgents/");
	    Response response1 = target1.request().get();
	    String ret1 = response1.readEntity(String.class);
	    System.out.println("RET /**************" + ret1);
	    
	    
	    
	    ArrayList<String> lista = new ArrayList<String>();

	       JSONArray jsonA;
	       try {
	        jsonA = new JSONArray(ret1);
	      
	        for (int i = 0; i < jsonA.length(); i++) {
	         String ime=jsonA.getJSONObject(i).getJSONObject("aid").getString("name");
	    
	         System.out.println("IMEEE: "  +ime);
	         lista.add(ime);
	        }
	        
	        System.out.println("LISTA TO STRING " + lista.toString());
	       } catch (JSONException e) { 
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	       }
	    
	       String porukaRefresh="Refresh;"+lista;
	    
	    
	    
	    
	    
	    try {
	     log.info("Websocket endpoint: " + this.hashCode() + " primio: " + porukaRefresh + " u sesiji: "
	       + session.getId());
	     for (Session s : sessions) {

	      s.getBasicRemote().sendText(porukaRefresh);
	      log.info("Sending '" + porukaRefresh + "' to : " + s.getId());

	     }

	    } catch (IOException e) {
	     try {
	      session.close();
	     } catch (IOException e1) {
	      e1.printStackTrace();
	     }
	    }
	    }catch (IOException e) {
	     try {
	      session.close();
	     } catch (IOException e1) {
	      e1.printStackTrace();
	     }
	    }
	   }
	  } else if (tip == "Neki drugi") {

	  } */

	 }

	

	}


