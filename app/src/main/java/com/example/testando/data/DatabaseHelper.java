package com.example.testando.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "quiz_local.db";
    public static final int DB_VERSION = 1;

    public static final String T_USERS = "users";
    public static final String C_USER_ID = "id";
    public static final String C_USER_NAME = "name";
    public static final String C_USER_CREATED = "created_at";

    public static final String T_SCORES = "scores";
    public static final String C_SCORE_ID = "id";
    public static final String C_SCORE_USER_ID = "user_id";
    public static final String C_SCORE_TOPIC = "topic";
    public static final String C_SCORE_CORRECT = "correct";
    public static final String C_SCORE_TOTAL = "total";
    public static final String C_SCORE_TS = "created_at";

    public DatabaseHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsers = "CREATE TABLE " + T_USERS + " (" +
                C_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                C_USER_NAME + " TEXT NOT NULL UNIQUE, " +
                C_USER_CREATED + " INTEGER NOT NULL)";
        String createScores = "CREATE TABLE " + T_SCORES + " (" +
                C_SCORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                C_SCORE_USER_ID + " INTEGER NOT NULL, " +
                C_SCORE_TOPIC + " TEXT NOT NULL, " +
                C_SCORE_CORRECT + " INTEGER NOT NULL, " +
                C_SCORE_TOTAL + " INTEGER NOT NULL, " +
                C_SCORE_TS + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + C_SCORE_USER_ID + ") REFERENCES " + T_USERS + "(" + C_USER_ID + ") ON DELETE CASCADE)";
        db.execSQL(createUsers);
        db.execSQL(createScores);

        // usuário “Guest” (opcional)
        db.execSQL("INSERT INTO " + T_USERS + " (" + C_USER_NAME + "," + C_USER_CREATED + ") VALUES ('Guest', strftime('%s','now'))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + T_SCORES);
        db.execSQL("DROP TABLE IF EXISTS " + T_USERS);
        onCreate(db);
    }
}