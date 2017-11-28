package com.poli.usp.whatsp2p.data.remote.models;

/**
 * Created by azul on 17/04/17.
 */

public class PostsResponse {
    int userId;
    int id;
    String title;
    String body;

    public int getUserId() {
        return userId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}
