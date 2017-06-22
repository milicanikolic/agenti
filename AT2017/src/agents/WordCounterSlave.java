package agents;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import enums.Performative;
import mdb.MDBProducer;
import model.ACLMessage;
import model.AbstractAgent;
import model.Agent;

@Stateful
@Remote(Agent.class)
public class WordCounterSlave extends AbstractAgent{

	

	@Override
	protected void onMessage(ACLMessage message) {
			System.out.println("USAO U SLAVE");
			String path = message.getContent();
			int cnt=0;
		       
	        BufferedReader br = null;
	     
	        try {

	            String sCurrentLine=null;
	 
	            br = new BufferedReader(new FileReader(path));

	            while ((sCurrentLine = br.readLine()) != null) {
	               
	                String replaced=sCurrentLine.replaceAll("[^a-zA-Z0-9]+", " ").trim();
	               
	                String[] splited=replaced.split("\\s+");
	                cnt+=splited.length;
	            }
	            
	            System.out.println("CNT" + cnt);
	            
	            ACLMessage aclMessage = new ACLMessage();
		        aclMessage.setPerformative(Performative.AGREE);
		        aclMessage.setSender(message.getReplyTo());
		        aclMessage.setReplyTo(message.getSender());
		        aclMessage.setContent(String.valueOf(cnt));
		       
		        
		        MDBProducer.sendJMS(aclMessage);
	            
	        }
	        catch(IOException e){
	        	e.printStackTrace();
	        }
	        	finally{
	        		try{
	        			if(br!=null)
	        				br.close();
	        		}
	        		catch (Exception e) {
	        			e.printStackTrace();
					}
	        	}
	    
			}
	            
}
