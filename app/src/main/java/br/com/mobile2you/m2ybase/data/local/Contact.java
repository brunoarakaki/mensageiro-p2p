package br.com.mobile2you.m2ybase.data.local;

import java.io.Serializable;

/**
 * Created by Bruno on 14-Aug-17.
 */

public class Contact implements Serializable {

    private int id;
    private String name;
    private String ip;
    private String peerId;

    public Contact(String name) {
        super();
        this.name = name;
    }

    public Contact(int id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
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

}
