package model;

public interface Agent {

	
	public void init(AID aid);
	public void handleMessage(ACLMessage message);
	public AID getAID();
	
}
