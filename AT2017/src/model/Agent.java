package model;

public interface Agent {

	public void init(AID aid);
	public void handleMessage(ACLMessage msg);
	public AID getAid();
	
}
