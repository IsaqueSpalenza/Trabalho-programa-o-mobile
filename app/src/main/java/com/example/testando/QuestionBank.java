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

    /* =========================
     *        HISTÓRIA
     * ========================= */
    private static List<Question> historia() {
        List<Question> list = new ArrayList<>();

        // --- Batalha de Viena (1683) ---
        list.add(new Question(
                "A Batalha de Viena (1683) é conhecida principalmente por:",
                new String[]{
                        "A vitória final do Império Otomano sobre o Sacro Império",
                        "A derrota do cerco otomano após a chegada da cavalaria polonesa",
                        "A primeira cruzada organizada pelo Papa Urbano II",
                        "A invasão mongol da Europa Central"
                },
                1
        ));
        list.add(new Question(
                "Quem liderou a famosa carga de cavalaria que ajudou a decidir a Batalha de Viena em 1683?",
                new String[]{
                        "Solimão, o Magnífico",
                        "João III Sobieski, rei da Polônia",
                        "Carlos V, imperador do Sacro Império",
                        "Francisco I, rei da França"
                },
                1
        ));
        list.add(new Question(
                "Qual foi o resultado estratégico imediato da Batalha de Viena (1683)?",
                new String[]{
                        "Expansão otomana para o norte",
                        "Fortalecimento da Liga Santa e recuo otomano da Europa Central",
                        "Colapso do Sacro Império Romano-Germânico",
                        "União entre França e Império Otomano"
                },
                1
        ));

        // --- Califado Omíada ---
        list.add(new Question(
                "Qual era a capital do Califado Omíada (século VII–VIII) no Oriente?",
                new String[]{
                        "Bagdá",
                        "Damasco",
                        "Cairo",
                        "Córdoba"
                },
                1
        ));
        list.add(new Question(
                "O Califado Omíada foi sucedido, no Oriente, por qual dinastia?",
                new String[]{
                        "Sassânida",
                        "Abássida",
                        "Seljúcida",
                        "Aiúbida"
                },
                1
        ));
        list.add(new Question(
                "No Ocidente islâmico, um ramo omíada estabeleceu um novo poder com centro em:",
                new String[]{
                        "Granada",
                        "Córdoba",
                        "Toledo",
                        "Sevilha"
                },
                1
        ));

        // --- Império Sassânida ---
        list.add(new Question(
                "Qual era a religião fortemente associada ao Império Sassânida?",
                new String[]{
                        "Cristianismo Ortodoxo",
                        "Zoroastrismo",
                        "Hinduísmo",
                        "Islamismo Xiita"
                },
                1
        ));
        list.add(new Question(
                "A capital mais proeminente do Império Sassânida foi:",
                new String[]{
                        "Ctesifonte",
                        "Susa",
                        "Babilônia",
                        "Nínive"
                },
                0
        ));
        list.add(new Question(
                "O Império Sassânida manteve longos conflitos com qual potência vizinha?",
                new String[]{
                        "Império Romano/Bizantino",
                        "Reino de Axum",
                        "Califado Fatímida",
                        "Reino dos Francos"
                },
                0
        ));
        list.add(new Question(
                "A queda final do Império Sassânida ocorreu no século VII diante de:",
                new String[]{
                        "Invasões mongóis",
                        "Conquistas macedônicas",
                        "Expansão do Califado Rachidun",
                        "Cruzadas europeias"
                },
                2
        ));

        // === Império Romano ===
        list.add(new Question(
                "Quem é geralmente reconhecido como o primeiro imperador romano?",
                new String[]{
                        "Júlio César",
                        "Augusto (Otaviano)",
                        "Nero",
                        "Trajano"
                },
                1
        ));
        list.add(new Question(
                "O período conhecido como 'Pax Romana' estende-se aproximadamente de:",
                new String[]{
                        "27 a.C. a 180 d.C.",
                        "146 a.C. a 27 a.C.",
                        "313 d.C. a 476 d.C.",
                        "395 d.C. a 565 d.C."
                },
                0
        ));
        list.add(new Question(
                "O Édito de Milão (313 d.C.), promulgado por Constantino e Licínio, estabeleceu:",
                new String[]{
                        "O fim do Senado Romano",
                        "Liberdade de culto no Império Romano",
                        "A divisão administrativa definitiva do Império",
                        "O título de 'dominvs et deus' ao imperador"
                },
                1
        ));
        list.add(new Question(
                "Em 395 d.C., a divisão permanente do Império Romano em Ocidente e Oriente é associada a:",
                new String[]{
                        "Diocleciano",
                        "Teodósio I",
                        "Constantino",
                        "Justiniano"
                },
                1
        ));
        list.add(new Question(
                "A Queda do Império Romano do Ocidente é tradicionalmente datada de:",
                new String[]{
                        "313 d.C.",
                        "410 d.C.",
                        "451 d.C.",
                        "476 d.C."
                },
                3
        ));
        list.add(new Question(
                "A Via Ápia é corretamente descrita como:",
                new String[]{
                        "Um aqueduto que abastecia Roma",
                        "Uma estrada romana de grande importância",
                        "Um anfiteatro romano",
                        "Um palácio imperial em Ravena"
                },
                1
        ));
        list.add(new Question(
                "Durante a Segunda Guerra Púnica, qual general cartaginês enfrentou Roma atravessando os Alpes?",
                new String[]{
                        "Aníbal Barca",
                        "Hamilcar Barca",
                        "Pirro de Épiro",
                        "Viriato"
                },
                0
        ));

        // --- Ea-Nasir ---
        list.add(new Question(
                "Pelo que Ea-Nasir, mercador da Mesopotâmia, ficou famoso nos estudos históricos?",
                new String[]{
                        "Por fundar a primeira biblioteca conhecida",
                        "Por ser citado em uma das mais antigas reclamações de cliente devido a cobre de baixa qualidade",
                        "Por cunhar a primeira moeda de ouro",
                        "Por liderar uma revolta contra Hamurábi"
                },
                1
        ));

        // --- Guerra de Inverno (Finlândia × URSS, 1939–1940) — NOVAS ---
        list.add(new Question(
                "A Guerra de Inverno entre Finlândia e União Soviética ocorreu em:",
                new String[]{
                        "1938–1939",
                        "1939–1940",
                        "1940–1941",
                        "1941–1942"
                },
                1
        ));
        list.add(new Question(
                "O acordo que encerrou a Guerra de Inverno é conhecido como:",
                new String[]{
                        "Tratado de Brest-Litovsk",
                        "Pacto Molotov-Ribbentrop",
                        "Tratado de Paz de Moscou (1940)",
                        "Acordo da Guerra de Continuação"
                },
                2
        ));
        list.add(new Question(
                "Qual comandante é figura central finlandesa na Guerra de Inverno?",
                new String[]{
                        "Carl Gustaf Emil Mannerheim",
                        "Simo Häyhä",
                        "Gustavus Adolphus",
                        "Georgy Zhukov"
                },
                0
        ));
        list.add(new Question(
                "Qual tática/condição ficou notavelmente associada ao sucesso defensivo finlandês no início do conflito?",
                new String[]{
                        "Bombardeio estratégico pesado",
                        "Guerra naval de bloqueio",
                        "Táticas 'motti' e uso de esqui em terreno nevado",
                        "Emprego de carros de combate pesados KV-1 em grande número"
                },
                2
        ));
        list.add(new Question(
                "Como consequência do tratado que encerrou a Guerra de Inverno, a Finlândia:",
                new String[]{
                        "Anexou a Carélia Oriental",
                        "Manteve todas as fronteiras inalteradas",
                        "Cedeu territórios, incluindo partes da Carélia, à URSS",
                        "Tornou-se parte da URSS"
                },
                2
        ));

        // Complementares de História
        list.add(new Question(
                "A Revolução Francesa iniciou-se em:",
                new String[]{
                        "1776",
                        "1789",
                        "1804",
                        "1815"
                },
                1
        ));
        list.add(new Question(
                "A Proclamação da República no Brasil ocorreu em:",
                new String[]{
                        "1822",
                        "1889",
                        "1930",
                        "1891"
                },
                1
        ));

        return list;
    }

    /* =========================
     *       MATEMÁTICA
     *  (2º grau, aritmética,
     *       álgebra)
     * ========================= */
    private static List<Question> matematica() {
        List<Question> list = new ArrayList<>();

        // --- Equação de 2º grau ---
        list.add(new Question(
                "Na equação ax² + bx + c = 0, o discriminante (Δ) é:",
                new String[]{
                        "a² - 4bc",
                        "b² - 4ac",
                        "c² - 4ab",
                        "2ab - 4c"
                },
                1
        ));
        list.add(new Question(
                "As raízes são reais e distintas quando:",
                new String[]{
                        "Δ < 0",
                        "Δ = 0",
                        "Δ > 0",
                        "a = 0"
                },
                2
        ));
        list.add(new Question(
                "Resolva: x² - 5x + 6 = 0",
                new String[]{
                        "x = 2 ou x = 3",
                        "x = -2 ou x = -3",
                        "x = 1 ou x = 6",
                        "x = 0 ou x = 6"
                },
                0
        ));
        list.add(new Question(
                "Se as raízes de x² - 7x + k = 0 são 3 e 4, então k vale:",
                new String[]{
                        "7",
                        "10",
                        "11",
                        "12"
                },
                3   // produto das raízes = c/a = 12
        ));
        list.add(new Question(
                "A soma das raízes de ax² + bx + c = 0 é:",
                new String[]{
                        "-b/a",
                        "b/a",
                        "c/a",
                        "-c/a"
                },
                0
        ));

        // --- Aritmética ---
        list.add(new Question(
                "Qual é 25% de 200?",
                new String[]{
                        "25",
                        "40",
                        "50",
                        "75"
                },
                2
        ));
        list.add(new Question(
                "O MMC (mínimo múltiplo comum) de 6 e 8 é:",
                new String[]{
                        "12",
                        "18",
                        "24",
                        "48"
                },
                2
        ));
        list.add(new Question(
                "A fração equivalente a 0,75 é:",
                new String[]{
                        "1/2",
                        "2/3",
                        "3/4",
                        "4/5"
                },
                2
        ));
        list.add(new Question(
                "Simplifique 180 ÷ 12:",
                new String[]{
                        "12",
                        "13",
                        "14",
                        "15"
                },
                3
        ));

        // --- Álgebra (1º grau, operações algébricas) ---
        list.add(new Question(
                "Resolva: 3x - 5 = 1",
                new String[]{
                        "x = 2",
                        "x = -2",
                        "x = 1",
                        "x = 0"
                },
                0
        ));
        list.add(new Question(
                "Simplifique: 2(x + 3) - (x - 1)",
                new String[]{
                        "x + 7",
                        "x + 5",
                        "x + 1",
                        "3x + 1"
                },
                0   // 2x+6 - x +1 = x + 7
        ));
        list.add(new Question(
                "Resolva o sistema: { x + y = 7 ; x - y = 1 }",
                new String[]{
                        "(x, y) = (4, 3)",
                        "(x, y) = (3, 4)",
                        "(x, y) = (5, 2)",
                        "(x, y) = (2, 5)"
                },
                0
        ));
        list.add(new Question(
                "Fatore: x² - 9",
                new String[]{
                        "(x - 9)(x - 1)",
                        "(x - 3)(x + 3)",
                        "(x + 9)(x - 1)",
                        "(x - 3)²"
                },
                1
        ));
        list.add(new Question(
                "Qual expressão representa corretamente (x + 2)²?",
                new String[]{
                        "x² + 4x + 4",
                        "x² + 2x + 2",
                        "x² + 2x + 4",
                        "x² + 4"
                },
                0
        ));

        return list;
    }
}