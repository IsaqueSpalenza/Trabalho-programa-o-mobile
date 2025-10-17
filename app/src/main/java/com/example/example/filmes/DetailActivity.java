package com.example.example.filmes;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvScore = findViewById(R.id.tvDetailScore);

        String title = getIntent().getStringExtra("movie_title");
        double score = getIntent().getDoubleExtra("movie_score", 0.0);

        tvTitle.setText(title);
        tvScore.setText("Pontuação: " + score);
    }
}