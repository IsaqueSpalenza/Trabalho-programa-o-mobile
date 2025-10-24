package com.example.testando.data;

public class Score {
    public long id;
    public long userId;
    public String topic;
    public int correct;
    public int total;
    public long createdAt;

    public Score(long id, long userId, String topic, int correct, int total, long createdAt) {
        this.id = id;
        this.userId = userId;
        this.topic = topic;
        this.correct = correct;
        this.total = total;
        this.createdAt = createdAt;
    }
}