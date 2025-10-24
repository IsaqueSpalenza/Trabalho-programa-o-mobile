package com.example.testando.data;

public class User {
    public long id;
    public String name;
    public long createdAt;

    public User(long id, String name, long createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }
}