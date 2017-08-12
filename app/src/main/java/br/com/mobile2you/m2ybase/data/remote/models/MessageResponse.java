package br.com.mobile2you.m2ybase.data.remote.models;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by Bruno on 12-Aug-17.
 */

public class MessageResponse {
    private int senderId;
    private int groupId;
    private String text;
    private Timestamp sendedAt;

    public MessageResponse(int senderId, String text, int groupId) {
        super();
        this.senderId = senderId;
        this.groupId = groupId;
        this.text = text;

        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        this.sendedAt = new Timestamp(now.getTime());
    }

    public int getSenderId() {
        return senderId;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getText() {
        return text;
    }

    public Timestamp getSendedAt() {
        return sendedAt;
    }
}
