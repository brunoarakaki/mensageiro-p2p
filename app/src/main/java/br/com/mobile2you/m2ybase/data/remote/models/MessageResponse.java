package br.com.mobile2you.m2ybase.data.remote.models;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by Bruno on 12-Aug-17.
 */

public class MessageResponse {
    private int senderId;
    private int receiverId;
    private String text;
    private Timestamp sentAt;

    public MessageResponse(int senderId, String text, int receiverId) {
        super();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;

        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        this.sentAt = new Timestamp(now.getTime());
    }

    public MessageResponse(int senderId, String text, int receiverId, Timestamp sentAt) {
        super();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.sentAt = sentAt;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public String getText() {
        return text;
    }

    public Timestamp getSentAt() {
        return sentAt;
    }

}
