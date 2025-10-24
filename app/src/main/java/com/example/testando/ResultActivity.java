package com.example.testando;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testando.data.ScoreRepository;
import com.example.testando.data.SessionPrefs;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);

        TextView tvResultado = findViewById(R.id.tvResultado);
        String tema = getIntent().getStringExtra("TOPIC");
        int score = getIntent().getIntExtra("SCORE", 0);
        int total = getIntent().getIntExtra("TOTAL", 0);

        tvResultado.setText("Tema: " + tema + "\nAcertos: " + score + " de " + total);

        long uid = SessionPrefs.getCurrentUserId(this);
        if (uid > 0) {
            new ScoreRepository(this).insertScore(uid, tema, score, total);
        }
    }
}