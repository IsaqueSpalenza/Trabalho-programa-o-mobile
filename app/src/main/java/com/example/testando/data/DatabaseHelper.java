package com.example.testando.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "quiz_local.db";
    public static final int DB_VERSION = 3; // ⬅️ subimos para v3

    // ---- users ----
    public static final String T_USERS = "users";
    public static final String C_USER_ID = "id";
    public static final String C_USER_NAME = "name";
    public static final String C_USER_CREATED = "created_at";

    // ---- scores ----
    public static final String T_SCORES = "scores";
    public static final String C_SCORE_ID = "id";
    public static final String C_SCORE_USER_ID = "user_id";
    public static final String C_SCORE_TOPIC = "topic";   // ex.: "Historia", "Matematica"
    public static final String C_SCORE_CORRECT = "correct";
    public static final String C_SCORE_TOTAL = "total";
    public static final String C_SCORE_TS = "created_at";

    // ---- topics ----
    public static final String T_TOPICS = "topics";
    public static final String C_TOPIC_ID = "id";
    public static final String C_TOPIC_NAME = "name";

    // ---- questions ----
    public static final String T_QUESTIONS = "questions";
    public static final String C_Q_ID = "id";
    public static final String C_Q_TOPIC_ID = "topic_id";
    public static final String C_Q_TEXT = "text";
    public static final String C_Q_DIFFICULTY = "difficulty"; // ⬅️ 0=Normal, 1=Avançado

    // ---- options ----
    public static final String T_OPTIONS = "options";
    public static final String C_OPT_ID = "id";
    public static final String C_OPT_Q_ID = "question_id";
    public static final String C_OPT_TEXT = "text";
    public static final String C_OPT_CORRECT = "is_correct"; // 0/1

    public DatabaseHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Garantir FKs (delete em cascata)
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // --- users ---
        String createUsers = "CREATE TABLE " + T_USERS + " (" +
                C_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                C_USER_NAME + " TEXT NOT NULL UNIQUE, " +
                C_USER_CREATED + " INTEGER NOT NULL)";
        db.execSQL(createUsers);

        // --- scores ---
        String createScores = "CREATE TABLE " + T_SCORES + " (" +
                C_SCORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                C_SCORE_USER_ID + " INTEGER NOT NULL, " +
                C_SCORE_TOPIC + " TEXT NOT NULL, " +
                C_SCORE_CORRECT + " INTEGER NOT NULL, " +
                C_SCORE_TOTAL + " INTEGER NOT NULL, " +
                C_SCORE_TS + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + C_SCORE_USER_ID + ") REFERENCES " + T_USERS + "(" + C_USER_ID + ") ON DELETE CASCADE)";
        db.execSQL(createScores);

        // --- topics ---
        String createTopics = "CREATE TABLE " + T_TOPICS + " (" +
                C_TOPIC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                C_TOPIC_NAME + " TEXT NOT NULL UNIQUE)";
        db.execSQL(createTopics);

        // --- questions (com difficulty) ---
        String createQuestions = "CREATE TABLE " + T_QUESTIONS + " (" +
                C_Q_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                C_Q_TOPIC_ID + " INTEGER NOT NULL, " +
                C_Q_TEXT + " TEXT NOT NULL, " +
                C_Q_DIFFICULTY + " INTEGER NOT NULL DEFAULT 0, " + // ⬅️ novo
                "FOREIGN KEY(" + C_Q_TOPIC_ID + ") REFERENCES " + T_TOPICS + "(" + C_TOPIC_ID + ") ON DELETE CASCADE)";
        db.execSQL(createQuestions);

        // --- options ---
        String createOptions = "CREATE TABLE " + T_OPTIONS + " (" +
                C_OPT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                C_OPT_Q_ID + " INTEGER NOT NULL, " +
                C_OPT_TEXT + " TEXT NOT NULL, " +
                C_OPT_CORRECT + " INTEGER NOT NULL CHECK(" + C_OPT_CORRECT + " IN (0,1)), " +
                "FOREIGN KEY(" + C_OPT_Q_ID + ") REFERENCES " + T_QUESTIONS + "(" + C_Q_ID + ") ON DELETE CASCADE)";
        db.execSQL(createOptions);

        // Usuário padrão "Guest" (opcional)
        db.execSQL("INSERT INTO " + T_USERS + " (" + C_USER_NAME + "," + C_USER_CREATED + ") " +
                "VALUES ('Guest', strftime('%s','now'))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // v2: criava topics/questions/options (já tratado abaixo para quem vier da v1)
        if (oldV < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + T_TOPICS + " (" +
                    C_TOPIC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    C_TOPIC_NAME + " TEXT NOT NULL UNIQUE)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + T_QUESTIONS + " (" +
                    C_Q_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    C_Q_TOPIC_ID + " INTEGER NOT NULL, " +
                    C_Q_TEXT + " TEXT NOT NULL, " +
                    "FOREIGN KEY(" + C_Q_TOPIC_ID + ") REFERENCES " + T_TOPICS + "(" + C_TOPIC_ID + ") ON DELETE CASCADE)");
            db.execSQL("CREATE TABLE IF NOT EXISTS " + T_OPTIONS + " (" +
                    C_OPT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    C_OPT_Q_ID + " INTEGER NOT NULL, " +
                    C_OPT_TEXT + " TEXT NOT NULL, " +
                    C_OPT_CORRECT + " INTEGER NOT NULL CHECK(" + C_OPT_CORRECT + " IN (0,1)), " +
                    "FOREIGN KEY(" + C_OPT_Q_ID + ") REFERENCES " + T_QUESTIONS + "(" + C_Q_ID + ") ON DELETE CASCADE)");
        }
        // v3: garantir coluna difficulty
        if (oldV < 3) {
            db.execSQL("ALTER TABLE " + T_QUESTIONS + " ADD COLUMN " +
                    C_Q_DIFFICULTY + " INTEGER NOT NULL DEFAULT 0");
        }
    }
}