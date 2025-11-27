package com.example.testando;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testando.data.DatabaseHelper;
import com.example.testando.data.QuestionRepository;
import com.example.testando.data.SessionPrefs;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);

        TextView tvResultado = findViewById(R.id.tvResultado);

        String tema  = getIntent().getStringExtra("TOPIC");
        int score    = getIntent().getIntExtra("SCORE", 0);
        int total    = getIntent().getIntExtra("TOTAL", 0);
        int difficulty = getIntent().getIntExtra(
                "DIFFICULTY",
                QuestionRepository.D_NORMAL
        );

        String diffLabel = (difficulty == QuestionRepository.D_NORMAL)
                ? "Normal"
                : "Avançado";

        tvResultado.setText(
                "Tema: " + tema +
                        "\nDificuldade: " + diffLabel +
                        "\nAcertos: " + score + " de " + total
        );

        long uid = SessionPrefs.getCurrentUserId(this);
        if (uid > 0) {
            // grava o resultado na tabela scores, incluindo difficulty
            DatabaseHelper dbh = new DatabaseHelper(this);
            SQLiteDatabase db = dbh.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(DatabaseHelper.C_SCORE_USER_ID, uid);
            cv.put(DatabaseHelper.C_SCORE_TOPIC, tema);
            cv.put(DatabaseHelper.C_SCORE_CORRECT, score);
            cv.put(DatabaseHelper.C_SCORE_TOTAL, total);
            cv.put(DatabaseHelper.C_SCORE_TS, System.currentTimeMillis() / 1000);
            cv.put(DatabaseHelper.C_SCORE_DIFFICULTY, difficulty);

            db.insert(DatabaseHelper.T_SCORES, null, cv);
        }
    }
}