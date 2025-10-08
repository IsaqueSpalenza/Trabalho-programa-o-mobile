package com.example.testando;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private Button btnHistoria, btnMatematica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnHistoria   = findViewById(R.id.btnHistoria);
        btnMatematica = findViewById(R.id.btnMatematica);

        View.OnClickListener abrirQuiz = v -> {
            String tema = (v.getId() == R.id.btnHistoria) ? "Historia" : "Matematica";
            Intent i = new Intent(MainActivity.this, QuizActivity.class);
            i.putExtra("TOPIC", tema);
            startActivity(i);
        };

        btnHistoria.setOnClickListener(abrirQuiz);
        btnMatematica.setOnClickListener(abrirQuiz);
    }
}
