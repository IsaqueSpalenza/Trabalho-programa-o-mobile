package com.example.testando;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testando.data.QuestionRepository;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    private TextView tvTema, tvPergunta, tvProgresso;
    private RadioGroup rgOpcoes;
    private RadioButton rb1, rb2, rb3, rb4;
    private Button btnContinuar;

    private List<Question> perguntas;
    private int indice = 0;
    private int acertos = 0;
    private String tema;
    private int dificuldade;

    // estado da pergunta atual
    private String[] opcoesExibidas;
    private int correctIndexExibido = -1;

    private final Random rng = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);

        // recebe dados enviados pelo MainActivity
        tema = getIntent().getStringExtra("TOPIC");
        dificuldade = getIntent().getIntExtra("DIFFICULTY", QuestionRepository.D_NORMAL);

        // busca do banco:
        QuestionRepository repo = new QuestionRepository(this);
        perguntas = repo.getQuestionsByTopicAndDifficulty(tema, dificuldade);

        // caso o banco ainda não tenha perguntas, carrega o seed automaticamente:
        if (perguntas.isEmpty()) {
            repo.seedIfEmpty();
            perguntas = repo.getQuestionsByTopicAndDifficulty(tema, dificuldade);
        }

        // embaralha a ordem das perguntas
        Collections.shuffle(perguntas, rng);

        tvTema      = findViewById(R.id.tvTema);
        tvPergunta  = findViewById(R.id.tvPergunta);
        tvProgresso = findViewById(R.id.tvProgresso);
        rgOpcoes    = findViewById(R.id.rgOpcoes);
        rb1         = findViewById(R.id.rb1);
        rb2         = findViewById(R.id.rb2);
        rb3         = findViewById(R.id.rb3);
        rb4         = findViewById(R.id.rb4);
        btnContinuar= findViewById(R.id.btnContinuar);

        tvTema.setText("Tema: " + tema + "  •  Dificuldade: " +
                (dificuldade == QuestionRepository.D_NORMAL ? "Normal" : "Avançado"));

        carregarPergunta();

        btnContinuar.setOnClickListener(v -> {
            int checkedId = rgOpcoes.getCheckedRadioButtonId();
            if (checkedId == -1) {
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Escolha uma alternativa")
                        .setMessage("Selecione uma opção para continuar.")
                        .setPositiveButton("Ok", null)
                        .show();
                return;
            }

            int escolha = (checkedId == R.id.rb1) ? 0 :
                    (checkedId == R.id.rb2) ? 1 :
                            (checkedId == R.id.rb3) ? 2 : 3;

            boolean correto = (escolha == correctIndexExibido);
            String respostaCerta = opcoesExibidas[correctIndexExibido];

            if (correto) acertos++;

            boolean ultima = (indice + 1 >= perguntas.size());

            new MaterialAlertDialogBuilder(this)
                    .setTitle(correto ? "✔ Resposta Correta" : "✘ Resposta Incorreta")
                    .setMessage(correto
                            ? "Muito bem! Você acertou."
                            : "A alternativa correta era:\n\n• " + respostaCerta)
                    .setPositiveButton(ultima ? "Finalizar" : "Próxima", (dialog, which) -> {
                        indice++;
                        if (indice < perguntas.size()) {
                            carregarPergunta();
                        } else {
                            Intent i = new Intent(QuizActivity.this, ResultActivity.class);
                            i.putExtra("SCORE", acertos);
                            i.putExtra("TOTAL", perguntas.size());
                            i.putExtra("TOPIC", tema);
                            i.putExtra("DIFFICULTY", dificuldade);
                            startActivity(i);
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
        });
    }

    private void carregarPergunta() {
        Question q = perguntas.get(indice);

        // embaralhar mantendo índice correto
        List<Alt> pool = new ArrayList<>(4);
        String[] ops = q.getOptions();
        for (int i = 0; i < 4; i++) {
            pool.add(new Alt(ops[i], i == q.getCorrectIndex()));
        }
        Collections.shuffle(pool, rng);

        opcoesExibidas = new String[4];
        correctIndexExibido = -1;
        for (int i = 0; i < 4; i++) {
            opcoesExibidas[i] = pool.get(i).text;
            if (pool.get(i).isCorrect) correctIndexExibido = i;
        }

        tvPergunta.setText(q.getText());
        rb1.setText(opcoesExibidas[0]);
        rb2.setText(opcoesExibidas[1]);
        rb3.setText(opcoesExibidas[2]);
        rb4.setText(opcoesExibidas[3]);
        rgOpcoes.clearCheck();

        tvProgresso.setText((indice + 1) + " / " + perguntas.size());
    }

    // embaralhar mantendo a informação da correta
    private static class Alt {
        String text;
        boolean isCorrect;
        Alt(String t, boolean c) { text = t; isCorrect = c; }
    }
}