package com.example.atividade9;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import db.NomeDao;
import com.example.atividade9.Nome;
import ui.NomeAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText edtNome;
    private Button btnSalvar;
    private RecyclerView rv;
    private NomeAdapter adapter;
    private NomeDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dao = new NomeDao(this);
        edtNome = findViewById(R.id.edtNome);
        btnSalvar = findViewById(R.id.btnSalvar);
        rv = findViewById(R.id.rvNomes);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NomeAdapter(
                dao.listarTodos(),
                item -> confirmarExclusao(item),
                item -> abrirDialogEdicao(item)
        );
        rv.setAdapter(adapter);

        btnSalvar.setOnClickListener(v -> {
            String nome = edtNome.getText().toString().trim();
            if (TextUtils.isEmpty(nome)) {
                edtNome.setError("Digite um nome");
                return;
            }
            dao.inserir(nome);
            edtNome.setText("");
            recarregarLista();
        });
    }

    private void recarregarLista() {
        adapter.setDados(dao.listarTodos());
    }

    private void confirmarExclusao(Nome item) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir")
                .setMessage("Excluir \"" + item.getNome() + "\"?")
                .setPositiveButton("Excluir", (d, w) -> {
                    dao.excluir(item.getId());
                    recarregarLista();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void abrirDialogEdicao(Nome item) {
        final EditText input = new EditText(this);
        input.setText(item.getNome());

        new AlertDialog.Builder(this)
                .setTitle("Editar nome")
                .setView(input)
                .setPositiveButton("Salvar", (d, w) -> {
                    String novoNome = input.getText().toString().trim();
                    if (!TextUtils.isEmpty(novoNome)) {
                        item.setNome(novoNome);
                        dao.atualizar(item);
                        recarregarLista();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
