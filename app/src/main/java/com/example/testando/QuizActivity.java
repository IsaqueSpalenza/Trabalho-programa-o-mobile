package com.example.testando;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView tvTema, tvPergunta, tvProgresso;
    private RadioGroup rgOpcoes;
    private RadioButton rb1, rb2, rb3, rb4;
    private Button btnContinuar;

    private List<Question> perguntas;
    private int indice = 0;
    private int acertos = 0;
    private String tema;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);

        tema = getIntent().getStringExtra("TOPIC");
        perguntas = QuestionBank.getQuestions(tema);

        tvTema      = findViewById(R.id.tvTema);
        tvPergunta  = findViewById(R.id.tvPergunta);
        tvProgresso = findViewById(R.id.tvProgresso);
        rgOpcoes    = findViewById(R.id.rgOpcoes);
        rb1         = findViewById(R.id.rb1);
        rb2         = findViewById(R.id.rb2);
        rb3         = findViewById(R.id.rb3);
        rb4         = findViewById(R.id.rb4);
        btnContinuar= findViewById(R.id.btnContinuar);

        tvTema.setText("Tema: " + tema);
        carregarPergunta();

        btnContinuar.setOnClickListener(v -> {
            int checkedId = rgOpcoes.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(this, "Escolha uma alternativa", Toast.LENGTH_SHORT).show();
                return;
            }

            int escolha = (checkedId == R.id.rb1) ? 0 :
                    (checkedId == R.id.rb2) ? 1 :
                            (checkedId == R.id.rb3) ? 2 : 3;

            Question atual = perguntas.get(indice);
            boolean correto = (escolha == atual.getCorrectIndex());
            if (correto) {
                acertos++;
                Toast.makeText(this, "✔ Resposta correta!", Toast.LENGTH_SHORT).show();
            } else {
                String respostaCerta = atual.getOptions()[atual.getCorrectIndex()];
                Toast.makeText(this, "✘ Resposta incorreta.\nCorreta: " + respostaCerta, Toast.LENGTH_SHORT).show();
            }

            indice++;
            if (indice < perguntas.size()) {
                carregarPergunta();
            } else {
                Intent i = new Intent(QuizActivity.this, ResultActivity.class);
                i.putExtra("SCORE", acertos);
                i.putExtra("TOTAL", perguntas.size());
                i.putExtra("TOPIC", tema);
                startActivity(i);
                finish();
            }
        });
    }

    private void carregarPergunta() {
        Question q = perguntas.get(indice);
        tvPergunta.setText(q.getText());
        rb1.setText(q.getOptions()[0]);
        rb2.setText(q.getOptions()[1]);
        rb3.setText(q.getOptions()[2]);
        rb4.setText(q.getOptions()[3]);
        rgOpcoes.clearCheck();
        tvProgresso.setText((indice + 1) + " / " + perguntas.size());
    }
}