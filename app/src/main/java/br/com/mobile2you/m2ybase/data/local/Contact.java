package br.com.mobile2you.m2ybase.data.local;

import android.content.Context;

import com.poli.tcc.dht.DHT;

import java.io.IOException;
import java.io.Serializable;
import java.security.PublicKey;

import br.com.mobile2you.m2ybase.utils.exceptions.ContactNotFoundException;

/**
 * Created by Bruno on 14-Aug-17.
 */

public class Contact implements Serializable {

    private String id;
    private String name;
    private String ip;
    private int port;
    private byte[] signPublicKeyEncoded;
    private byte[] chatPublicKeyRingEncoded;

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

    public byte[] getSignPublicKeyEncoded() {
        return signPublicKeyEncoded;
    }

    public void setSignPublicKeyEncoded(byte[] signPublicKeyEncoded) {
        this.signPublicKeyEncoded = signPublicKeyEncoded;
    }

    public byte[] getChatPublicKeyRingEncoded() {
        return chatPublicKeyRingEncoded;
    }

    public void setChatPublicKeyRingEncoded(byte[] chatPublicKeyRingEncoded) {
        this.chatPublicKeyRingEncoded = chatPublicKeyRingEncoded;
    }

    public void save(Context context) {
        ContactDatabaseHelper dbHelper = new ContactDatabaseHelper(context);
        dbHelper.update(this);
    }

    public void updateChatPublicKey(Context context) throws ContactNotFoundException {
        try {
            final PublicKey signPublicKey = (PublicKey) DHT.get(this.getId());
            if (signPublicKey == null) throw new ContactNotFoundException();
            final byte[] chatPublicKeyRingEncoded = (byte[]) DHT.getProtected("chatPublicKey", signPublicKey);
            this.setChatPublicKeyRingEncoded(chatPublicKeyRingEncoded);
            this.save(context);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
