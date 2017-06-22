package model;

public abstract class AbstractAgent implements Agent{
	
protected AID myAid;
	
	@Override
	public void init(AID aid) {
		myAid = aid;
	}
	
	@Override
	public void handleMessage(ACLMessage msg) {
		onMessage(msg);
	}

	protected abstract void onMessage(ACLMessage msg);
	
	public AID getAid()
	{
		return myAid;
	}
	
	
}
