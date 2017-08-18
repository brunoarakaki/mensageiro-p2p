package br.com.mobile2you.m2ybase.data.local;

/**
 * Created by Bruno on 14-Aug-17.
 */

public class Contact {
    private int id;
    private String name;

    public Contact(String name) {
        super();
        this.name = name;
    }

    public Contact(int id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
