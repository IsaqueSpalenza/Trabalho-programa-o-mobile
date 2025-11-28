package com.example.testando.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.testando.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionRepository {
    public static final int D_NORMAL = 0;
    public static final int D_AVANCADO = 1;

    private final DatabaseHelper dbh;

    public QuestionRepository(Context ctx) { this.dbh = new DatabaseHelper(ctx); }

    // tópicos (fixos)
    public long upsertTopic(String name) {
        if (!"Historia".equals(name) && !"Matematica".equals(name)) {
            throw new IllegalArgumentException("Tema inválido. Use 'Historia' ou 'Matematica'.");
        }
        SQLiteDatabase db = dbh.getWritableDatabase();
        try (Cursor c = db.query(DatabaseHelper.T_TOPICS,
                new String[]{DatabaseHelper.C_TOPIC_ID},
                DatabaseHelper.C_TOPIC_NAME + "=?",
                new String[]{name}, null, null, null)) {
            if (c.moveToFirst()) return c.getLong(0);
        }
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.C_TOPIC_NAME, name);
        return db.insert(DatabaseHelper.T_TOPICS, null, cv);
    }

    // inserção (jogo)
    public long insertQuestionWithOptions(long topicId, String text, String[] options, int correctIndex, int difficulty) {
        if (options == null || options.length != 4) throw new IllegalArgumentException("São esperadas 4 opções");
        if (correctIndex < 0 || correctIndex > 3) throw new IllegalArgumentException("correctIndex deve ser 0..3");

        SQLiteDatabase db = dbh.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues qv = new ContentValues();
            qv.put(DatabaseHelper.C_Q_TOPIC_ID, topicId);
            qv.put(DatabaseHelper.C_Q_TEXT, text);
            qv.put(DatabaseHelper.C_Q_DIFFICULTY, difficulty);
            long qid = db.insert(DatabaseHelper.T_QUESTIONS, null, qv);

            for (int i = 0; i < 4; i++) {
                ContentValues ov = new ContentValues();
                ov.put(DatabaseHelper.C_OPT_Q_ID, qid);
                ov.put(DatabaseHelper.C_OPT_TEXT, options[i]);
                ov.put(DatabaseHelper.C_OPT_CORRECT, i == correctIndex ? 1 : 0);
                db.insert(DatabaseHelper.T_OPTIONS, null, ov);
            }
            db.setTransactionSuccessful();
            return qid;
        } finally {
            db.endTransaction();
        }
    }

    // leitura para o jogo
    public List<Question> getQuestionsByTopicAndDifficulty(String topicName, int difficulty) {
        SQLiteDatabase db = dbh.getReadableDatabase();
        List<Question> out = new ArrayList<>();

        long topicId = getTopicIdOrMinus1(db, topicName);
        if (topicId < 0) return out;

        String sel = DatabaseHelper.C_Q_TOPIC_ID + "=? AND " + DatabaseHelper.C_Q_DIFFICULTY + "=?";
        String[] args = { String.valueOf(topicId), String.valueOf(difficulty) };

        try (Cursor cq = db.query(DatabaseHelper.T_QUESTIONS, null, sel, args, null, null, DatabaseHelper.C_Q_ID + " ASC")) {
            while (cq.moveToNext()) {
                long qid = cq.getLong(cq.getColumnIndexOrThrow(DatabaseHelper.C_Q_ID));
                String text = cq.getString(cq.getColumnIndexOrThrow(DatabaseHelper.C_Q_TEXT));

                String[] options = new String[4];
                int correctIndex = -1;

                try (Cursor co = db.query(DatabaseHelper.T_OPTIONS, null,
                        DatabaseHelper.C_OPT_Q_ID + "=?",
                        new String[]{String.valueOf(qid)}, null, null, DatabaseHelper.C_OPT_ID + " ASC")) {
                    int i = 0;
                    while (co.moveToNext() && i < 4) {
                        String ot = co.getString(co.getColumnIndexOrThrow(DatabaseHelper.C_OPT_TEXT));
                        int isC = co.getInt(co.getColumnIndexOrThrow(DatabaseHelper.C_OPT_CORRECT));
                        options[i] = ot;
                        if (isC == 1) correctIndex = i;
                        i++;
                    }
                }
                if (correctIndex >= 0) out.add(new Question(text, options, correctIndex));
            }
        }
        return out;
    }

    /* ======================= ÁREA ADMIN ======================= */

    public static class AdminQuestion {
        public long id;
        public String topicName;   // "Historia" ou "Matematica"
        public int difficulty;     // 0/1
        public String text;
        public String[] options;   // 4
        public int correctIndex;   // 0..3
    }

    private long getTopicIdOrMinus1(SQLiteDatabase db, String topicName) {
        try (Cursor ct = db.query(DatabaseHelper.T_TOPICS, null,
                DatabaseHelper.C_TOPIC_NAME + "=?",
                new String[]{topicName}, null, null, null)) {
            if (ct.moveToFirst()) {
                return ct.getLong(ct.getColumnIndexOrThrow(DatabaseHelper.C_TOPIC_ID));
            }
        }
        return -1;
    }

    private String getTopicNameById(SQLiteDatabase db, long id) {
        try (Cursor c = db.query(DatabaseHelper.T_TOPICS, null,
                DatabaseHelper.C_TOPIC_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            if (c.moveToFirst()) {
                return c.getString(c.getColumnIndexOrThrow(DatabaseHelper.C_TOPIC_NAME));
            }
        }
        return null;
    }

    public List<AdminQuestion> listAdmin(String filterTopicOrNull, Integer filterDifficultyOrNull) {
        SQLiteDatabase db = dbh.getReadableDatabase();
        List<AdminQuestion> out = new ArrayList<>();

        StringBuilder where = new StringBuilder("1=1");
        List<String> args = new ArrayList<>();

        if (filterTopicOrNull != null) {
            long tid = getTopicIdOrMinus1(db, filterTopicOrNull);
            if (tid < 0) return out;
            where.append(" AND ").append(DatabaseHelper.C_Q_TOPIC_ID).append("=?");
            args.add(String.valueOf(tid));
        }
        if (filterDifficultyOrNull != null) {
            where.append(" AND ").append(DatabaseHelper.C_Q_DIFFICULTY).append("=?");
            args.add(String.valueOf(filterDifficultyOrNull));
        }

        try (Cursor cq = db.query(DatabaseHelper.T_QUESTIONS, null,
                where.toString(), args.toArray(new String[0]), null, null,
                DatabaseHelper.C_Q_ID + " DESC")) {
            while (cq.moveToNext()) {
                long qid = cq.getLong(cq.getColumnIndexOrThrow(DatabaseHelper.C_Q_ID));
                long tid = cq.getLong(cq.getColumnIndexOrThrow(DatabaseHelper.C_Q_TOPIC_ID));
                String tname = getTopicNameById(db, tid);
                String text = cq.getString(cq.getColumnIndexOrThrow(DatabaseHelper.C_Q_TEXT));
                int diff = cq.getInt(cq.getColumnIndexOrThrow(DatabaseHelper.C_Q_DIFFICULTY));

                String[] options = new String[4];
                int correctIndex = -1;

                try (Cursor co = db.query(DatabaseHelper.T_OPTIONS, null,
                        DatabaseHelper.C_OPT_Q_ID + "=?",
                        new String[]{String.valueOf(qid)}, null, null, DatabaseHelper.C_OPT_ID + " ASC")) {
                    int i = 0;
                    while (co.moveToNext() && i < 4) {
                        String ot = co.getString(co.getColumnIndexOrThrow(DatabaseHelper.C_OPT_TEXT));
                        int isC = co.getInt(co.getColumnIndexOrThrow(DatabaseHelper.C_OPT_CORRECT));
                        options[i] = ot;
                        if (isC == 1) correctIndex = i;
                        i++;
                    }
                }

                AdminQuestion aq = new AdminQuestion();
                aq.id = qid;
                aq.topicName = tname;
                aq.difficulty = diff;
                aq.text = text;
                aq.options = options;
                aq.correctIndex = correctIndex;
                out.add(aq);
            }
        }
        return out;
    }

    public AdminQuestion getAdminById(long qid) {
        SQLiteDatabase db = dbh.getReadableDatabase();
        try (Cursor cq = db.query(DatabaseHelper.T_QUESTIONS, null,
                DatabaseHelper.C_Q_ID + "=?",
                new String[]{String.valueOf(qid)}, null, null, null)) {
            if (!cq.moveToFirst()) return null;

            long tid = cq.getLong(cq.getColumnIndexOrThrow(DatabaseHelper.C_Q_TOPIC_ID));
            String tname = getTopicNameById(db, tid);
            String text = cq.getString(cq.getColumnIndexOrThrow(DatabaseHelper.C_Q_TEXT));
            int diff = cq.getInt(cq.getColumnIndexOrThrow(DatabaseHelper.C_Q_DIFFICULTY));

            String[] options = new String[4];
            int correctIndex = -1;

            try (Cursor co = db.query(DatabaseHelper.T_OPTIONS, null,
                    DatabaseHelper.C_OPT_Q_ID + "=?",
                    new String[]{String.valueOf(qid)}, null, null, DatabaseHelper.C_OPT_ID + " ASC")) {
                int i = 0;
                while (co.moveToNext() && i < 4) {
                    String ot = co.getString(co.getColumnIndexOrThrow(DatabaseHelper.C_OPT_TEXT));
                    int isC = co.getInt(co.getColumnIndexOrThrow(DatabaseHelper.C_OPT_CORRECT));
                    options[i] = ot;
                    if (isC == 1) correctIndex = i;
                    i++;
                }
            }

            AdminQuestion aq = new AdminQuestion();
            aq.id = qid;
            aq.topicName = tname;
            aq.difficulty = diff;
            aq.text = text;
            aq.options = options;
            aq.correctIndex = correctIndex;
            return aq;
        }
    }

    public void updateQuestionWithOptions(long qid, long topicId, String text, String[] options, int correctIndex, int difficulty) {
        if (options == null || options.length != 4) throw new IllegalArgumentException("4 opções");
        if (correctIndex < 0 || correctIndex > 3) throw new IllegalArgumentException("correctIndex 0..3");

        SQLiteDatabase db = dbh.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues qv = new ContentValues();
            qv.put(DatabaseHelper.C_Q_TOPIC_ID, topicId);
            qv.put(DatabaseHelper.C_Q_TEXT, text);
            qv.put(DatabaseHelper.C_Q_DIFFICULTY, difficulty);
            db.update(DatabaseHelper.T_QUESTIONS, qv, DatabaseHelper.C_Q_ID + "=?", new String[]{String.valueOf(qid)});

            // apaga opções antigas e insere novas
            db.delete(DatabaseHelper.T_OPTIONS, DatabaseHelper.C_OPT_Q_ID + "=?", new String[]{String.valueOf(qid)});
            for (int i = 0; i < 4; i++) {
                ContentValues ov = new ContentValues();
                ov.put(DatabaseHelper.C_OPT_Q_ID, qid);
                ov.put(DatabaseHelper.C_OPT_TEXT, options[i]);
                ov.put(DatabaseHelper.C_OPT_CORRECT, i == correctIndex ? 1 : 0);
                db.insert(DatabaseHelper.T_OPTIONS, null, ov);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteQuestion(long qid) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        db.delete(DatabaseHelper.T_QUESTIONS, DatabaseHelper.C_Q_ID + "=?", new String[]{String.valueOf(qid)});
        // options caem via FK ON DELETE CASCADE
    }

    //seed opcional
    // dentro de QuestionRepository.java
    public void seedIfEmpty() {
        SQLiteDatabase db = dbh.getReadableDatabase();
        long count = 0;
        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.T_QUESTIONS, null)) {
            if (c.moveToFirst()) count = c.getLong(0);
        }
        if (count > 0) return;

        long historia = upsertTopic("Historia");
        long matematica = upsertTopic("Matematica");

        // ===================== HISTÓRIA =====================

        insertQuestionWithOptions(historia,
                "A Batalha de Viena (1683) é conhecida principalmente por:",
                new String[]{
                        "A vitória final do Império Otomano sobre o Sacro Império.",
                        "A derrota do cerco otomano após a chegada da cavalaria polonesa.",
                        "A primeira cruzada organizada pelo Papa Urbano II.",
                        "A invasão mongol da Europa Central."
                },
                1, D_AVANCADO);

        insertQuestionWithOptions(historia,
                "Quem liderou a famosa carga de cavalaria que ajudou a decidir a Batalha de Viena em 1683?",
                new String[]{
                        "Solimão, o Magnífico.",
                        "João III Sobieski, rei da Polônia.",
                        "Carlos V, imperador do Sacro Império.",
                        "Francisco I, rei da França."
                },
                1, D_AVANCADO);

        insertQuestionWithOptions(historia,
                "Qual foi o resultado estratégico imediato da Batalha de Viena (1683)?",
                new String[]{
                        "Expansão otomana para o norte.",
                        "Fortalecimento da Liga Santa e recuo otomano da Europa Central.",
                        "Colapso do Sacro Império Romano-Germânico.",
                        "União entre França e Império Otomano."
                },
                1, D_NORMAL);

        insertQuestionWithOptions(historia,
                "Qual era a capital do Califado Omíada (século VII–VIII) no Oriente?",
                new String[]{
                        "Bagdá.",
                        "Damasco.",
                        "Cairo.",
                        "Córdoba."
                },
                1, D_AVANCADO);

        insertQuestionWithOptions(historia,
                "O Califado Omíada foi sucedido, no Oriente, por qual dinastia?",
                new String[]{
                        "Sassânida.",
                        "Abássida.",
                        "Seljúcida.",
                        "Aiúbida."
                },
                1, D_AVANCADO);

        insertQuestionWithOptions(historia,
                "No Ocidente islâmico, um ramo omíada estabeleceu um novo poder com centro em:",
                new String[]{
                        "Granada.",
                        "Córdoba.",
                        "Toledo.",
                        "Sevilha."
                },
                1, D_AVANCADO);

        insertQuestionWithOptions(historia,
                "Qual era a religião fortemente associada ao Império Sassânida?",
                new String[]{
                        "Cristianismo Ortodoxo.",
                        "Zoroastrismo.",
                        "Hinduísmo.",
                        "Islamismo Xiita."
                },
                1, D_NORMAL);

        insertQuestionWithOptions(historia,
                "A capital mais proeminente do Império Sassânida foi:",
                new String[]{
                        "Ctesifonte.",
                        "Susa.",
                        "Babilônia.",
                        "Nínive."
                },
                0, D_NORMAL);

        insertQuestionWithOptions(historia,
                "O Império Sassânida manteve longos conflitos com qual potência vizinha?",
                new String[]{
                        "Império Romano/Bizantino.",
                        "Reino de Axum.",
                        "Califado Fatímida.",
                        "Reino dos Francos."
                },
                0, D_NORMAL);

        insertQuestionWithOptions(historia,
                "A queda final do Império Sassânida ocorreu no século VII diante de:",
                new String[]{
                        "Invasões mongóis.",
                        "Conquistas macedônicas.",
                        "Expansão do Califado Rachidun.",
                        "Cruzadas europeias."
                },
                2, D_NORMAL);

        insertQuestionWithOptions(historia,
                "Quem é geralmente reconhecido como o primeiro imperador romano?",
                new String[]{
                        "Júlio César.",
                        "Augusto (Otaviano).",
                        "Nero.",
                        "Trajano."
                },
                1, D_AVANCADO);

        insertQuestionWithOptions(historia,
                "O período conhecido como 'Pax Romana' estende-se aproximadamente de:",
                new String[]{
                        "27 a.C. a 180 d.C.",
                        "146 a.C. a 27 a.C.",
                        "313 d.C. a 476 d.C.",
                        "395 d.C. a 565 d.C."
                },
                0, D_NORMAL);

        insertQuestionWithOptions(historia,
                "O Édito de Milão (313 d.C.), promulgado por Constantino e Licínio, estabeleceu:",
                new String[]{
                        "O fim do Senado Romano.",
                        "Liberdade de culto no Império Romano.",
                        "A divisão administrativa definitiva do Império.",
                        "O título de 'dominvs et deus' ao imperador."
                },
                1, D_AVANCADO);

        insertQuestionWithOptions(historia,
                "Em 395 d.C., a divisão permanente do Império Romano em Ocidente e Oriente é associada a:",
                new String[]{
                        "Diocleciano.",
                        "Teodósio I.",
                        "Constantino.",
                        "Justiniano."
                },
                1, D_AVANCADO);

        insertQuestionWithOptions(historia,
                "A Queda do Império Romano do Ocidente é tradicionalmente datada de:",
                new String[]{
                        "313 d.C.",
                        "410 d.C.",
                        "451 d.C.",
                        "476 d.C."
                },
                3, D_NORMAL);

        insertQuestionWithOptions(historia,
                "A Via Ápia é corretamente descrita como:",
                new String[]{
                        "Um aqueduto que abastecia Roma.",
                        "Uma estrada romana de grande importância.",
                        "Um anfiteatro romano.",
                        "Um palácio imperial em Ravena."
                },
                1, D_NORMAL);

        insertQuestionWithOptions(historia,
                "Durante a Segunda Guerra Púnica, qual general cartaginês enfrentou Roma atravessando os Alpes?",
                new String[]{
                        "Aníbal Barca.",
                        "Hamilcar Barca.",
                        "Pirro de Épiro.",
                        "Viriato."
                },
                0, D_AVANCADO);

        insertQuestionWithOptions(historia,
                "Pelo que Ea-Nasir, mercador da Mesopotâmia, ficou famoso nos estudos históricos?",
                new String[]{
                        "Por fundar a primeira biblioteca conhecida.",
                        "Por ser citado em uma das mais antigas reclamações de cliente devido a cobre de baixa qualidade.",
                        "Por cunhar a primeira moeda de ouro.",
                        "Por liderar uma revolta contra Hamurábi."
                },
                1, D_NORMAL);

        insertQuestionWithOptions(historia,
                "A Guerra de Inverno entre Finlândia e União Soviética ocorreu em:",
                new String[]{
                        "1938–1939.",
                        "1939–1940.",
                        "1940–1941.",
                        "1941–1942."
                },
                1, D_AVANCADO);

        insertQuestionWithOptions(historia,
                "O acordo que encerrou a Guerra de Inverno é conhecido como:",
                new String[]{
                        "Tratado de Brest-Litovsk.",
                        "Pacto Molotov-Ribbentrop.",
                        "Tratado de Paz de Moscou (1940).",
                        "Acordo da Guerra de Continuação."
                },
                2, D_NORMAL);

        insertQuestionWithOptions(historia,
                "Qual comandante é figura central finlandesa na Guerra de Inverno?",
                new String[]{
                        "Carl Gustaf Emil Mannerheim.",
                        "Simo Häyhä.",
                        "Gustavus Adolphus.",
                        "Georgy Zhukov."
                },
                0, D_AVANCADO);

        insertQuestionWithOptions(historia,
                "Qual tática/condição ficou notavelmente associada ao sucesso defensivo finlandês no início do conflito?",
                new String[]{
                        "Bombardeio estratégico pesado.",
                        "Guerra naval de bloqueio.",
                        "Táticas 'motti' e uso de esqui em terreno nevado.",
                        "Emprego de carros de combate pesados KV-1 em grande número."
                },
                2, D_AVANCADO);

        insertQuestionWithOptions(historia,
                "Como consequência do tratado que encerrou a Guerra de Inverno, a Finlândia:",
                new String[]{
                        "Anexou a Carélia Oriental.",
                        "Manteve todas as fronteiras inalteradas.",
                        "Cedeu territórios, incluindo partes da Carélia, à URSS.",
                        "Tornou-se parte da URSS."
                },
                2, D_AVANCADO);

        insertQuestionWithOptions(historia,
                "A Revolução Francesa iniciou-se em:",
                new String[]{
                        "1776.",
                        "1789.",
                        "1804.",
                        "1815."
                },
                1, D_NORMAL);

        insertQuestionWithOptions(historia,
                "A Proclamação da República no Brasil ocorreu em:",
                new String[]{
                        "1822.",
                        "1889.",
                        "1930.",
                        "1891."
                },
                1, D_NORMAL);

        // ===================== MATEMÁTICA =====================

        insertQuestionWithOptions(matematica,
                "Na equação ax² + bx + c = 0, o discriminante (Δ) é:",
                new String[]{
                        "a² - 4bc",
                        "b² - 4ac",
                        "c² - 4ab",
                        "2ab - 4c"
                },
                1, D_NORMAL);

        insertQuestionWithOptions(matematica,
                "As raízes são reais e distintas quando:",
                new String[]{
                        "Δ < 0",
                        "Δ = 0",
                        "Δ > 0",
                        "a = 0"
                },
                2, D_NORMAL);

        insertQuestionWithOptions(matematica,
                "Resolva: x² - 5x + 6 = 0",
                new String[]{
                        "x = 2 ou x = 3",
                        "x = -2 ou x = -3",
                        "x = 1 ou x = 6",
                        "x = 0 ou x = 6"
                },
                0, D_AVANCADO);

        insertQuestionWithOptions(matematica,
                "Se as raízes de x² - 7x + k = 0 são 3 e 4, então k vale:",
                new String[]{
                        "7",
                        "10",
                        "11",
                        "12"
                },
                3, D_AVANCADO);

        insertQuestionWithOptions(matematica,
                "A soma das raízes de ax² + bx + c = 0 é:",
                new String[]{
                        "-b/a",
                        "b/a",
                        "c/a",
                        "-c/a"
                },
                0, D_NORMAL);

        insertQuestionWithOptions(matematica,
                "Qual é 25% de 200?",
                new String[]{
                        "25",
                        "40",
                        "50",
                        "75"
                },
                2, D_NORMAL);

        insertQuestionWithOptions(matematica,
                "O MMC (mínimo múltiplo comum) de 6 e 8 é:",
                new String[]{
                        "12",
                        "18",
                        "24",
                        "48"
                },
                2, D_NORMAL);

        insertQuestionWithOptions(matematica,
                "A fração equivalente a 0,75 é:",
                new String[]{
                        "1/2",
                        "2/3",
                        "3/4",
                        "4/5"
                },
                2, D_NORMAL);

        insertQuestionWithOptions(matematica,
                "Simplifique 180 ÷ 12:",
                new String[]{
                        "12",
                        "13",
                        "14",
                        "15"
                },
                3, D_NORMAL);

        insertQuestionWithOptions(matematica,
                "Resolva: 3x - 5 = 1",
                new String[]{
                        "x = 2",
                        "x = -2",
                        "x = 1",
                        "x = 0"
                },
                0, D_AVANCADO);

        insertQuestionWithOptions(matematica,
                "Simplifique: 2(x + 3) - (x - 1)",
                new String[]{
                        "x + 7",
                        "x + 5",
                        "x + 1",
                        "3x + 1"
                },
                0, D_NORMAL);

        insertQuestionWithOptions(matematica,
                "Resolva o sistema: { x + y = 7 ; x - y = 1 }",
                new String[]{
                        "(x, y) = (4, 3)",
                        "(x, y) = (3, 4)",
                        "(x, y) = (5, 2)",
                        "(x, y) = (2, 5)"
                },
                0, D_AVANCADO);

        insertQuestionWithOptions(matematica,
                "Fatore: x² - 9",
                new String[]{
                        "(x - 9)(x - 1)",
                        "(x - 3)(x + 3)",
                        "(x + 9)(x - 1)",
                        "(x - 3)²"
                },
                1, D_AVANCADO);

        insertQuestionWithOptions(matematica,
                "Qual expressão representa corretamente (x + 2)²?",
                new String[]{
                        "x² + 4x + 4",
                        "x² + 2x + 2",
                        "x² + 2x + 4",
                        "x² + 4"
                },
                0, D_AVANCADO);

        insertQuestionWithOptions(matematica,
                "O volume de um líquido volátil diminui 20% por hora. Após um tempo t, seu volume se reduz à metade. O valor que mais se aproxima de t é (use log 2 = 0,30):",
                new String[]{
                        "2h 30min",
                        "2h",
                        "3h",
                        "3h 24min"
                },
                2, D_AVANCADO);

        insertQuestionWithOptions(matematica,
                "Calcule a derivada de f(x) = x^3 * exp(2x).",
                new String[]{
                        "f'(x) = 3x^2 * exp(2x)",
                        "f'(x) = 2x^3 * exp(2x)",
                        "f'(x) = exp(2x) * (3x^2 + 2x^3)",
                        "f'(x) = exp(2x) * (3x^2 + 6x)"
                },
                2, D_AVANCADO);

        insertQuestionWithOptions(matematica,
                "Calcule o limite: lim (x -> 0) [sin(3x) / x].",
                new String[]{
                        "0",
                        "1",
                        "3",
                        "1/3"
                },
                2, D_AVANCADO);

        insertQuestionWithOptions(matematica,
                "Seja a matriz A = [[1, 2, 3], [0, -1, 4], [2, 1, 0]]. Calcule det(A).",
                new String[]{
                        "12",
                        "18",
                        "-6",
                        "-18"
                },
                1, D_AVANCADO);

        insertQuestionWithOptions(matematica,
                "Sejam os números complexos z = 2 - 3i e w = 1 + 4i. Calcule z * w.",
                new String[]{
                        "z * w = 14 + 5i",
                        "z * w = -10 + 11i",
                        "z * w = 14 - 5i",
                        "z * w = -14 + 5i"
                },
                0, D_AVANCADO);

        insertQuestionWithOptions(matematica,
                "Calcule a soma da série geométrica infinita S = 3 + 3*(1/2) + 3*(1/2)^2 + 3*(1/2)^3 + ...",
                new String[]{
                        "S = 3",
                        "S = 4",
                        "S = 5",
                        "S = 6"
                },
                3, D_NORMAL);

        insertQuestionWithOptions(matematica,
                "Dois dados justos são lançados. Qual é a probabilidade de a soma ser 9, dado que pelo menos um dos dados mostrou 4?",
                new String[]{
                        "2/11",
                        "1/6",
                        "3/11",
                        "1/3"
                },
                0, D_NORMAL);

        insertQuestionWithOptions(matematica,
                "Considere a matriz 2x2 A = [[3, 1], [0, 2]]. Quais são os autovalores de A?",
                new String[]{
                        "λ1 = 1, λ2 = 2",
                        "λ1 = 2, λ2 = 3",
                        "λ1 = 3, λ2 = 3",
                        "λ1 = 2, λ2 = 4"
                },
                1, D_NORMAL);

        insertQuestionWithOptions(matematica,
                "Resolva a equação diferencial dy/dx = 3x^2, com condição inicial y(0) = 1.",
                new String[]{
                        "y(x) = x^3 + 1",
                        "y(x) = x^3 - 1",
                        "y(x) = 3x^3 + 1",
                        "y(x) = x^2 + 1"
                },
                0, D_NORMAL);

        insertQuestionWithOptions(matematica,
                "Resolva para x: ln(x - 1) + ln(x + 1) = ln(8), com x real.",
                new String[]{
                        "x = -3",
                        "x = 3",
                        "x = 2",
                        "x = 4"
                },
                1, D_AVANCADO);

        insertQuestionWithOptions(matematica,
                "Sejam os vetores a = (1, 2, 2) e b = (2, 0, 1). Calcule cos(theta), onde theta é o ângulo entre a e b.",
                new String[]{
                        "cos(theta) = 4 / (3 * sqrt(5))",
                        "cos(theta) = 3 / (4 * sqrt(5))",
                        "cos(theta) = 2 / (3 * sqrt(5))",
                        "cos(theta) = 1 / sqrt(5)"
                },
                0, D_AVANCADO);
    }
}