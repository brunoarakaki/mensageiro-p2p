package br.com.mobile2you.m2ybase.data.local;

import java.io.Serializable;
import java.security.PublicKey;

/**
 * Created by Bruno on 14-Aug-17.
 */

public class Contact implements Serializable {

    private String id;
    private String name;
    private String ip;
    private int port;
    private PublicKey signPublicKey;
    private PublicKey chatPublicKey;

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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public PublicKey getSignPublicKey() {
        return signPublicKey;
    }

    public void setSignPublicKey(PublicKey publicKey) {
        this.signPublicKey = publicKey;
    }

    public PublicKey getChatPublicKey() {
        return chatPublicKey;
    }

    public void setChatPublicKey(PublicKey chatPublicKey) {
        this.chatPublicKey = chatPublicKey;
    }

}
