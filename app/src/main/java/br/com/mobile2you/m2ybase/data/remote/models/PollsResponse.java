package br.com.mobile2you.m2ybase.data.remote.models;

import java.util.List;

/**
 * Created by mobile2you on 28/11/16.
 */

public class PollsResponse {
    private String question;
    private String url;
    private List<Choices> choices;
    private String published_at;

    public String getQuestion() {
        return question;
    }

    public String getUrl() {
        return url;
    }

    public List<Choices> getChoices() {
        return choices;
    }

    public String getPublished_at() {
        return published_at;
    }

    public class Choices {
        private String choice;
        private int votes;
        private String url;

        public String getChoice() {
            return choice;
        }

        public int getVotes() {
            return votes;
        }

        public String getUrl() {
            return url;
        }
    }
}
