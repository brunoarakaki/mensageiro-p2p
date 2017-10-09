package com.poli.tcc.dht;

import net.tomp2p.peers.Number160;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by mayerlevy on 9/26/17.
 */

public class DHTMessage implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum MessageTypes {
        WHO_IS, WHERE_IS, MESSAGE, FOUND_PEER, HANDSHAKE, PING, PONG, SHUTDOWN
    }

	private String id;
	private DHTNode sender;
    private DHTNode receiver;
    private DHTNode replyTo;
    private String content;
    private MessageTypes type;
    private String responseTo;

    public DHTMessage(DHTNode sender, DHTNode receiver, DHTNode replyTo, MessageTypes type, String content) {
        this.id = new Number160(new Random(42L)).toString();
        this.sender = sender;
        this.receiver = receiver;
        this.replyTo = replyTo;
        this.content = content;
        this.type = type;
    }
    
    public String getId() {
		return id;
	}

	public DHTNode getSender() {
        return sender;
    }

    public void setSender(DHTNode sender) {
        this.sender = sender;
    }

    public DHTNode getReceiver() {
        return receiver;
    }

    public void setReceiver(DHTNode receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) { this.content = content; }

    public MessageTypes getType() {
        return type;
    }

    public void setType(MessageTypes type) {
        this.type = type;
    }

    public DHTNode getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(DHTNode replyTo) {
        this.replyTo = replyTo;
    }

	public String getResponseTo() {
		return responseTo;
	}

	public void setResponseTo(String responseTo) {
		this.responseTo = responseTo;
	}

}
