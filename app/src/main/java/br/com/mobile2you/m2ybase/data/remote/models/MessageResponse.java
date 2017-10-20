package br.com.mobile2you.m2ybase.data.remote.models;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;

import br.com.mobile2you.m2ybase.data.local.Contact;

/**
 * Created by Bruno on 12-Aug-17.
 */

public class MessageResponse implements Serializable {

    private Contact sender;
    private Contact receiver;
    private String plainText;
    private byte[] encodedText;
    private Timestamp sentAt;

    public MessageResponse(Contact sender, Contact receiver) {
        super();
        this.sender = sender;
        this.receiver = receiver;

        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        this.sentAt = new Timestamp(now.getTime());
    }

    public MessageResponse(Contact sender, Contact receiver, String plainText, Timestamp sentAt) {
        super();
        this.sender = sender;
        this.receiver = receiver;
        this.plainText = plainText;
        this.sentAt = sentAt;
    }

    public Contact getSender() {
        return sender;
    }

    public void setSender(Contact sender) {
        this.sender = sender;
    }

    public Contact getReceiver() {
        return receiver;
    }

    public void setReceiver(Contact receiver) {
        this.receiver = receiver;
    }

    public String getPlainText() {
        return plainText;
    }

    public void setPlainText(String plainText) {
        this.plainText = plainText;
    }

    public Timestamp getSentAt() {
        return sentAt;
    }

    public byte[] getEncodedText() {
        return encodedText;
    }

    public void setEncodedText(byte[] encodedText) {
        this.encodedText = encodedText;
    }

}
