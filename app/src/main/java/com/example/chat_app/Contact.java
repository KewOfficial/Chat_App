package com.example.chat_app;

public class Contact {
    private String username;
    private boolean online;

    public Contact(String username, boolean online) {
        this.username = username;
        this.online = online;
    }

    public String getUsername() {
        return username;
    }

    public boolean isOnline() {
        return online;
    }
}
