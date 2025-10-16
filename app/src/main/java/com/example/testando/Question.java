package com.example.testando;

public class Question {
    private final String text;
    private final String[] options; // tamanho 4
    private final int correctIndex; // 0..3

    public Question(String text, String[] options, int correctIndex) {
        this.text = text;
        this.options = options;
        this.correctIndex = correctIndex;
    }

    public String getText() { return text; }
    public String[] getOptions() { return options; }
    public int getCorrectIndex() { return correctIndex; }
}