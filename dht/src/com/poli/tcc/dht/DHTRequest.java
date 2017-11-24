package com.poli.tcc.dht;

import java.io.Serializable;
import java.util.Random;
import java.util.Timer;

import net.tomp2p.peers.Number160;

public class DHTRequest implements Serializable {
	
	public static int DIRECT_PEER_BEAN_REQUEST = 1;
	public static int DIRECT_PEER_BEAN_RESPONSE = 2;
	
	private static final long serialVersionUID = 1L;
	private int type;
	private Object content;
	
	public int getType() {
		return this.type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public Object getContent() {
		return this.content;
	}
	
	public void setContent(Object content) {
		this.content = content;
	}
	
}
