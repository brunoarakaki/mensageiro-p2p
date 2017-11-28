package com.poli.usp.whatsp2p.data.remote.models;

import com.poli.usp.whatsp2p.data.local.Contact;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by mayerlevy on 10/30/17.
 */

public class BaseResponse implements Serializable {

    static final long serialVersionUID = 500;

    protected Contact sender;
    protected Contact receiver;
    protected Timestamp sentAt;

    public BaseResponse(Contact sender, Contact receiver) {
        super();
        this.sender = sender;
        this.receiver = receiver;

        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        this.sentAt = new Timestamp(now.getTime());
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

    public Timestamp getSentAt() {
        return sentAt;
    }

}
