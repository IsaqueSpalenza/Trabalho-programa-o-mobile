package com.example.testando.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testando.R;
import com.example.testando.data.QuestionRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AdminActivity extends AppCompatActivity implements QuestionsAdapter.OnItemAction {

    private QuestionRepository repo;
    private QuestionsAdapter adapter;
    private Spinner spTopic, spDiff;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        repo = new QuestionRepository(this);

        spTopic = findViewById(R.id.spTopic);
        spDiff  = findViewById(R.id.spDiff);

        spTopic.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Todos", "Historia", "Matematica"}));

        spDiff.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Todas", "Normal", "Avançado"}));

        RecyclerView rv = findViewById(R.id.rvQuestions);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuestionsAdapter(this);
        rv.setAdapter(adapter);

        findViewById(R.id.btnFiltrar).setOnClickListener(v -> loadList());
        ((FloatingActionButton) findViewById(R.id.fabAdd)).setOnClickListener(v -> {
            startActivity(new Intent(this, EditQuestionActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadList();
    }

    private void loadList() {
        String topic = null;
        Integer diff = null;

        String tsel = (String) spTopic.getSelectedItem();
        if (!"Todos".equals(tsel)) topic = tsel;

        String dsel = (String) spDiff.getSelectedItem();
        if ("Normal".equals(dsel)) diff = QuestionRepository.D_NORMAL;
        else if ("Avançado".equals(dsel)) diff = QuestionRepository.D_AVANCADO;

        adapter.submit(repo.listAdmin(topic, diff));
    }

    @Override
    public void onEdit(long qid) {
        Intent i = new Intent(this, EditQuestionActivity.class);
        i.putExtra("QID", qid);
        startActivity(i);
    }

    @Override
    public void onDelete(long qid) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Excluir pergunta?")
                .setMessage("Essa ação não pode ser desfeita.")
                .setPositiveButton("Excluir", (d,w)->{
                    repo.deleteQuestion(qid);
                    loadList();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}