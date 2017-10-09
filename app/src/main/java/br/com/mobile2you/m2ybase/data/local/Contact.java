package br.com.mobile2you.m2ybase.data.local;

import java.io.Serializable;

/**
 * Created by Bruno on 14-Aug-17.
 */

public class Contact implements Serializable {

    private String id;
    private String name;
    private String ip;
    private int port;
    private String peerId;

    public Contact(String id) {
        super();
        this.id = id;
    }

    public Contact(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIp() { return ip; }

    public void setIp(String ip) { this.ip = ip; }

    public String getPeerId() {
        return peerId;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
