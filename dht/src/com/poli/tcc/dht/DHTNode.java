package com.poli.tcc.dht;
import net.tomp2p.peers.Number160;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by mayerlevy on 9/26/17.
 */

public class DHTNode implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;
    private Number160 peerID;
    private String ip;
    private int port;
    private KeyPair signKeyPair;

    public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public DHTNode(Number160 peerId) {
    	this.peerID = peerId;
	}

	public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Number160 getPeerID() {
        return peerID;
    }

    public void setPeerID(Number160 peerID) {
        this.peerID = peerID;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

	public KeyPair getSignKeyPair() {
		return signKeyPair;
	}

	public void setSignKeyPair(KeyPair keyPair) {
		this.signKeyPair = keyPair;
	}

}
