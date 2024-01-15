package com.example.usable_security;

public class contacts {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    String name;
    String email;
    int shared=0;

    public contacts() {
        // Default constructor required for Firebase
    }

    public contacts(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public int getShared() {
        return shared;
    }

    public void setShared(int shared) {
        this.shared = shared;
    }
}
