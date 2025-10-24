package com.example.testando.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedHashMap;
import java.util.Map;

public class ScoreRepository {
    private final DatabaseHelper dbh;

    public ScoreRepository(Context ctx) { dbh = new DatabaseHelper(ctx); }

    public long insertScore(long userId, String topic, int correct, int total) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.C_SCORE_USER_ID, userId);
        cv.put(DatabaseHelper.C_SCORE_TOPIC, topic);
        cv.put(DatabaseHelper.C_SCORE_CORRECT, correct);
        cv.put(DatabaseHelper.C_SCORE_TOTAL, total);
        cv.put(DatabaseHelper.C_SCORE_TS, System.currentTimeMillis()/1000);
        return db.insert(DatabaseHelper.T_SCORES, null, cv);
    }

    /** Agregado por tópico: soma de acertos e total do usuário. */
    public Map<String, int[]> getTotalsByTopic(long userId) {
        SQLiteDatabase db = dbh.getReadableDatabase();
        String sql = "SELECT " + DatabaseHelper.C_SCORE_TOPIC + ", SUM(" + DatabaseHelper.C_SCORE_CORRECT + "), SUM(" + DatabaseHelper.C_SCORE_TOTAL + ")" +
                " FROM " + DatabaseHelper.T_SCORES +
                " WHERE " + DatabaseHelper.C_SCORE_USER_ID + "=? GROUP BY " + DatabaseHelper.C_SCORE_TOPIC +
                " ORDER BY " + DatabaseHelper.C_SCORE_TOPIC;
        Map<String, int[]> map = new LinkedHashMap<>();
        try (Cursor c = db.rawQuery(sql, new String[]{String.valueOf(userId)})) {
            while (c.moveToNext()) {
                String topic = c.getString(0);
                int sumCorrect = c.getInt(1);
                int sumTotal = c.getInt(2);
                map.put(topic, new int[]{sumCorrect, sumTotal});
            }
        }
        return map;
    }
}