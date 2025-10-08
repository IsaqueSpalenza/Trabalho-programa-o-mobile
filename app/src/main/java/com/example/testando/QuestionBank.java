package com.example.testando;

import java.util.ArrayList;
import java.util.List;

public class QuestionBank {

    public static List<Question> getQuestions(String topic) {
        if ("Historia".equalsIgnoreCase(topic)) {
            return historia();
        } else if ("Matematica".equalsIgnoreCase(topic)) {
            return matematica();
        }
        return new ArrayList<>();
    }

    private static List<Question> historia() {
        List<Question> list = new ArrayList<>();
        list.add(new Question(
                "Em que ano ocorreu a Proclamação da República no Brasil?",
                new String[]{"1822", "1889", "1500", "1930"},
                1
        ));
        list.add(new Question(
                "Quem foi o primeiro imperador do Brasil?",
                new String[]{"Dom Pedro I", "Dom Pedro II", "Deodoro da Fonseca", "Getúlio Vargas"},
                0
        ));
        list.add(new Question(
                "A Revolução Francesa começou em qual ano?",
                new String[]{"1776", "1789", "1804", "1815"},
                1
        ));
        return list;
    }

    private static List<Question> matematica() {
        List<Question> list = new ArrayList<>();
        list.add(new Question(
                "Qual é o valor de 7 × 8?",
                new String[]{"54", "56", "58", "64"},
                1
        ));
        list.add(new Question(
                "A raiz quadrada de 81 é:",
                new String[]{"7", "8", "9", "10"},
                2
        ));
        list.add(new Question(
                "Qual é a fração equivalente a 0,5?",
                new String[]{"1/3", "1/2", "2/3", "3/4"},
                1
        ));
        return list;
    }
}