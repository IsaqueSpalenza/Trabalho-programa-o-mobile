package com.example.testando.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final DatabaseHelper dbh;

    public UserRepository(Context ctx) { dbh = new DatabaseHelper(ctx); }

    public long insertUser(String name) throws Exception {
        SQLiteDatabase db = dbh.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.C_USER_NAME, name.trim());
        cv.put(DatabaseHelper.C_USER_CREATED, System.currentTimeMillis()/1000);
        long id = db.insertOrThrow(DatabaseHelper.T_USERS, null, cv);
        return id;
    }

    public List<User> getAll() {
        SQLiteDatabase db = dbh.getReadableDatabase();
        List<User> out = new ArrayList<>();
        try (Cursor c = db.query(DatabaseHelper.T_USERS, null, null, null, null, null, DatabaseHelper.C_USER_CREATED + " DESC")) {
            while (c.moveToNext()) {
                out.add(new User(
                        c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.C_USER_ID)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.C_USER_NAME)),
                        c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.C_USER_CREATED))
                ));
            }
        }
        return out;
    }

    public User getById(long id) {
        SQLiteDatabase db = dbh.getReadableDatabase();
        try (Cursor c = db.query(DatabaseHelper.T_USERS, null, DatabaseHelper.C_USER_ID + "=?", new String[]{String.valueOf(id)}, null, null, null)) {
            if (c.moveToFirst()) {
                return new User(
                        c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.C_USER_ID)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.C_USER_NAME)),
                        c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.C_USER_CREATED))
                );
            }
        }
        return null;
    }

    public void deleteById(long id) {
        SQLiteDatabase db = dbh.getWritableDatabase();
        db.delete(DatabaseHelper.T_USERS, DatabaseHelper.C_USER_ID + "=?", new String[]{String.valueOf(id)});
        // cascata nos scores via FK
    }
}