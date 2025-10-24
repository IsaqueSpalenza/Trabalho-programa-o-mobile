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

import com.example.testando.data.SessionPrefs;
import com.example.testando.data.User;
import com.example.testando.data.UserRepository;
import com.example.testando.ui.stats.StatsActivity;
import com.example.testando.ui.user.UserSelectActivity;
import com.google.android.material.appbar.MaterialToolbar;

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

        btnHistoria     = findViewById(R.id.btnHistoria);
        btnMatematica   = findViewById(R.id.btnMatematica);
        btnEstatisticas = findViewById(R.id.btnEstatisticas);

        ensureUserSelected();          // garante um usuário ativo
        updateUserSubtitleOnToolbar(); // mostra nome do usuário (se houver toolbar)

        View.OnClickListener abrirQuiz = v -> {
            long uid = SessionPrefs.getCurrentUserId(this);
            if (uid <= 0) { ensureUserSelected(); return; }
            String tema = (v.getId() == R.id.btnHistoria) ? "Historia" : "Matematica";
            Intent i = new Intent(MainActivity.this, QuizActivity.class);
            i.putExtra("TOPIC", tema);
            startActivity(i);
        };

        btnHistoria.setOnClickListener(abrirQuiz);
        btnMatematica.setOnClickListener(abrirQuiz);

        btnEstatisticas.setOnClickListener(v -> {
            long uid = SessionPrefs.getCurrentUserId(this);
            if (uid <= 0) { ensureUserSelected(); return; }
            startActivity(new Intent(this, StatsActivity.class));
        });
    }

    /** Se não houver usuário selecionado (ou foi apagado), abre a tela de seleção/criação. */
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

    /** Atualiza o subtítulo da Toolbar com o nome do usuário atual (se houver toolbar no layout). */
    private void updateUserSubtitleOnToolbar() {
        if (toolbar == null) return;
        long uid = SessionPrefs.getCurrentUserId(this);
        String sub = "Usuário: —";
        if (uid > 0) {
            User u = userRepo.getById(uid);
            if (u != null) sub = "Usuário: " + u.name;
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(sub);
        } else {
            toolbar.setSubtitle(sub);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Caso o usuário tenha sido trocado/ criado na tela de gestão
        long uid = SessionPrefs.getCurrentUserId(this);
        if (uid <= 0) {
            startActivity(new Intent(this, UserSelectActivity.class));
        }
        updateUserSubtitleOnToolbar();
    }

    // Menu de troca/gestão de usuário (item: action_switch_user)
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