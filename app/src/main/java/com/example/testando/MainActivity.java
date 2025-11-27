package com.example.testando;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testando.data.QuestionRepository;
import com.example.testando.data.SessionPrefs;
import com.example.testando.data.User;
import com.example.testando.data.UserRepository;
import com.example.testando.ui.stats.StatsActivity;
import com.example.testando.ui.user.UserSelectActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MainActivity extends AppCompatActivity {

    private Button btnHistoria, btnMatematica, btnEstatisticas;
    private UserRepository userRepo;
    @Nullable private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        userRepo = new UserRepository(this);

        // Toolbar é opcional; só usa se existir no layout
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) setSupportActionBar(toolbar);

        // --- Atalho secreto: 5 toques na Toolbar abre a AdminActivity ---
        if (toolbar != null) {
            final int[] taps = {0};
            final Runnable resetTaps = () -> taps[0] = 0; // zera após 1.5s sem toques

            toolbar.setOnClickListener(v -> {
                taps[0]++;
                if (taps[0] >= 5) {
                    taps[0] = 0;
                    startActivity(new Intent(
                            MainActivity.this,
                            com.example.testando.ui.admin.AdminActivity.class
                    ));
                }
                // reinicia o timer de 1.5s sempre que tocar
                toolbar.removeCallbacks(resetTaps);
                toolbar.postDelayed(resetTaps, 1500);
            });
        }

        btnHistoria     = findViewById(R.id.btnHistoria);
        btnMatematica   = findViewById(R.id.btnMatematica);
        btnEstatisticas = findViewById(R.id.btnEstatisticas);

        ensureUserSelected();
        updateUserSubtitleOnToolbar();

        // Antes do quiz, pergunta a dificuldade
        View.OnClickListener abrirQuiz = v -> {

            long uid = SessionPrefs.getCurrentUserId(this);
            if (uid <= 0) { ensureUserSelected(); return; }

            String tema = (v.getId() == R.id.btnHistoria) ? "Historia" : "Matematica";

            final String[] labels = {"Normal", "Avançado"};
            final int[] values = {QuestionRepository.D_NORMAL, QuestionRepository.D_AVANCADO};

            new MaterialAlertDialogBuilder(this)
                    .setTitle("Escolher dificuldade")
                    .setSingleChoiceItems(labels, 0, null) // default = Normal
                    .setPositiveButton("Continuar", (d, w) -> {
                        int index = ((androidx.appcompat.app.AlertDialog)d).getListView().getCheckedItemPosition();
                        int diff = values[index < 0 ? 0 : index];

                        Intent i = new Intent(MainActivity.this, QuizActivity.class);
                        i.putExtra("TOPIC", tema);
                        i.putExtra("DIFFICULTY", diff); // envia a dificuldade
                        startActivity(i);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        };

        btnHistoria.setOnClickListener(abrirQuiz);
        btnMatematica.setOnClickListener(abrirQuiz);

        btnEstatisticas.setOnClickListener(v -> {
            long uid = SessionPrefs.getCurrentUserId(this);
            if (uid <= 0) { ensureUserSelected(); return; }
            startActivity(new Intent(this, StatsActivity.class));
        });
    }

    // Se não houver usuário selecionado (ou foi apagado), abre a tela de seleção/criação.
    private void ensureUserSelected() {
        long uid = SessionPrefs.getCurrentUserId(this);
        if (uid <= 0) {
            startActivity(new Intent(this, UserSelectActivity.class));
        } else {
            User u = userRepo.getById(uid);
            if (u == null) {
                SessionPrefs.clear(this);
                startActivity(new Intent(this, UserSelectActivity.class));
            } else {
                Toast.makeText(this, "Usuário atual: " + u.name, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Atualiza o subtítulo da Toolbar com o nome do usuário atual
    private void updateUserSubtitleOnToolbar() {
        if (toolbar == null) return;
        long uid = SessionPrefs.getCurrentUserId(this);
        String sub = "Usuário: —";
        if (uid > 0) {
            User u = userRepo.getById(uid);
            if (u != null) sub = "Usuário: " + u.name;
        }
        if (getSupportActionBar() != null) getSupportActionBar().setSubtitle(sub);
        else toolbar.setSubtitle(sub);
    }

    @Override
    protected void onResume() {
        super.onResume();
        long uid = SessionPrefs.getCurrentUserId(this);
        if (uid <= 0) startActivity(new Intent(this, UserSelectActivity.class));
        updateUserSubtitleOnToolbar();
    }

    // Menu para trocar/gestão de usuário (action_switch_user)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_switch_user) {
            startActivity(new Intent(this, UserSelectActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
