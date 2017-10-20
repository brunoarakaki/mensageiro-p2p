package com.poli.tcc.dht;

import java.io.Serializable;

public class DHTSecureFrame implements Serializable {

	private byte[] data;
	
	public DHTSecureFrame(byte[] data) {
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	
}
