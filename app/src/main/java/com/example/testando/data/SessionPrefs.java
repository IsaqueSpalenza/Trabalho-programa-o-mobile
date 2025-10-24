package com.example.testando.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionPrefs {
    private static final String PREF = "session_prefs";
    private static final String KEY_USER_ID = "current_user_id";

    public static void setCurrentUserId(Context ctx, long id) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putLong(KEY_USER_ID, id).apply();
    }

    public static long getCurrentUserId(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sp.getLong(KEY_USER_ID, -1);
    }

    public static void clear(Context ctx) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().clear().apply();
    }
}