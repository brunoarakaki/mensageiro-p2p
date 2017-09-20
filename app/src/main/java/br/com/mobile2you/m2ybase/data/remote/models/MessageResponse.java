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
    private String text;
    private Timestamp sentAt;

    public MessageResponse(Contact sender, Contact receiver, String text) {
        super();
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;

        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        this.sentAt = new Timestamp(now.getTime());
    }

    public MessageResponse(Contact sender, Contact receiver, String text, Timestamp sentAt) {
        super();
        this.sender = sender;
        this.receiver = receiver;
        this.text = text;
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

    public String getText() {
        return text;
    }

    public Timestamp getSentAt() {
        return sentAt;
    }

}
