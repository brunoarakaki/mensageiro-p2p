package com.poli.tcc.dht;

import java.io.Serializable;
import java.util.Random;
import java.util.Timer;

import net.tomp2p.peers.Number160;

public class DHTRequest implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DHTMessage message;
	private String id;
	private int ttl;
	private int timeout;
	private Timer timer;
	private Runnable timeoutCallback;
	private Runnable successCallback;
	
	public Runnable getTimeoutCallback() {
		return timeoutCallback;
	}

	public void setTimeoutCallback(Runnable timeoutCallback) {
		this.timeoutCallback = timeoutCallback;
	}

	public DHTRequest(DHTMessage mes, int ttl, int timeout) {
		this.id = new Number160(new Random(42L)).toString();
		this.setMessage(mes);
		this.setTtl(ttl);
		this.setTimeout(timeout);
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public DHTMessage getMessage() {
		return message;
	}

	public void setMessage(DHTMessage mes) {
		this.message = mes;
	}

	public String getId() {
		return id;
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	public Runnable getSuccessCallback() {
		return successCallback;
	}

	public void setSuccessCallback(Runnable successCallback) {
		this.successCallback = successCallback;
	}
	
}
