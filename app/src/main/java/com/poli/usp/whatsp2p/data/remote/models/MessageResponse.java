package com.poli.usp.whatsp2p.data.remote.models;

import com.poli.usp.whatsp2p.data.local.Contact;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Bruno on 12-Aug-17.
 */

public class MessageResponse extends BaseResponse implements Serializable {

    static final long serialVersionUID = 501;

    private String message;

    public MessageResponse(Contact sender, Contact receiver) {
        super(sender, receiver);
    }

    public MessageResponse(Contact sender, Contact receiver, String message, Timestamp sentAt) {
        super(sender, receiver);
        this.message = message;
        this.sentAt = sentAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
