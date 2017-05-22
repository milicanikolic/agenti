package model;

import java.io.Serializable;

public class AgentCenter  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5894524728890355404L;
	
	private String alias;
	private String address;
	private String port;

	public AgentCenter(){
		
	}

	public AgentCenter(String address, String port,String alias) {
		super();
		this.alias = alias;
		this.address = address;
		this.port=port;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
	
	
}
