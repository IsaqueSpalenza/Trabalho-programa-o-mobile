package com.example.myapplication.model;

public class Nome {
    private long id;
    private String nome;

    public Nome() {}
    public Nome(long id, String nome) { this.id = id; this.nome = nome; }
    public Nome(String nome) { this.nome = nome; }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}