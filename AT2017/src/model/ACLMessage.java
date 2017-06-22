package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import enums.Performative;

public class ACLMessage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4256224928234003326L;
	
	private Performative performative;
	private AID sender;
	private AID replyTo;
	private String content;
	private String language;
	private String encoding;
	private String ontology;
	private String protocol;
	private String conversationId;
	
	
	public ACLMessage(){
	}

	public ACLMessage(Performative performative, AID sender, AID replyTo, String content,
			String language, String encoding, String ontology,
			String protocol, String conversationId) {
		super();
		this.performative = performative;
		this.sender = sender;
		this.replyTo = replyTo;
		this.content = content;
		this.language = language;
		this.encoding = encoding;
		this.ontology = ontology;
		this.protocol = protocol;
		this.conversationId = conversationId;
	}

	public Performative getPerformative() {
		return performative;
	}

	public void setPerformative(Performative performative) {
		this.performative = performative;
	}

	public AID getSender() {
		return sender;
	}

	public void setSender(AID sender) {
		this.sender = sender;
	}


	public AID getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(AID replyTo) {
		this.replyTo = replyTo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}


	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getOntology() {
		return ontology;
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}
	
	

}
