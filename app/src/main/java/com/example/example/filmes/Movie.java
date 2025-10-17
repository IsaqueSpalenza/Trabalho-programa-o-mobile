package com.example.example.filmes;

public class Movie {
    private final String title;
    private final double score;

    public Movie(String title, double score) {
        this.title = title;
        this.score = score;
    }

    public String getTitle() { return title; }
    public double getScore() { return score; }
}