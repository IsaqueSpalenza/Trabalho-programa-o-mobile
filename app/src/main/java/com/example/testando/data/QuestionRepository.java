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

    /* ---------- seed opcional ---------- */
    public void seedIfEmpty() {
        SQLiteDatabase db = dbh.getReadableDatabase();
        long count = 0;
        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.T_QUESTIONS, null)) {
            if (c.moveToFirst()) count = c.getLong(0);
        }
        if (count > 0) return;

        long historia = upsertTopic("Historia");
        long matematica = upsertTopic("Matematica");

        insertQuestionWithOptions(historia,
                "Quem é geralmente reconhecido como o primeiro imperador romano?",
                new String[]{"Júlio César", "Augusto (Otaviano)", "Nero", "Trajano"}, 1, D_NORMAL);

        insertQuestionWithOptions(matematica,
                "Na equação ax² + bx + c = 0, o discriminante (Δ) é:",
                new String[]{"a² - 4bc", "b² - 4ac", "c² - 4ab", "2ab - 4c"}, 1, D_NORMAL);
    }
}