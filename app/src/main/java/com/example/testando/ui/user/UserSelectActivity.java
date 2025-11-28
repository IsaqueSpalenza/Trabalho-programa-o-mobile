package com.example.testando.ui.user;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testando.R;
import com.example.testando.data.SessionPrefs;
import com.example.testando.data.User;
import com.example.testando.data.UserRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class UserSelectActivity extends AppCompatActivity implements UsersAdapter.OnUserClick {

    private UserRepository repo;
    private UsersAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_select);

        repo = new UserRepository(this);

        RecyclerView rv = findViewById(R.id.rvUsers);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsersAdapter(this);
        rv.setAdapter(adapter);
        loadUsers();

        FloatingActionButton fabAdd = findViewById(R.id.fabAddUser);
        fabAdd.setOnClickListener(v -> showAddDialog());
    }

    private void loadUsers() {
        List<User> users = repo.getAll();
        adapter.submit(users);
    }

    private void showAddDialog() {
        // Dialog com TextInputLayout
        final TextInputLayout til = new TextInputLayout(this);
        final EditText et = new EditText(this);
        et.setHint("Seu nome");
        et.setSingleLine(true);
        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(24)});
        til.addView(et);

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Criar novo usuário")
                .setView(til)
                .setPositiveButton("Salvar", (d, w) -> {
                    String name = et.getText().toString().trim();
                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(this, "Digite um nome", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        long id = repo.insertUser(name);
                        SessionPrefs.setCurrentUserId(this, id);
                        Toast.makeText(this, "Usuário selecionado: " + name, Toast.LENGTH_SHORT).show();
                        finish(); // volta para MainActivity
                    } catch (Exception ex) {
                        Toast.makeText(this, "Erro: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onUserClick(User u) {
        SessionPrefs.setCurrentUserId(this, u.id);
        Toast.makeText(this, "Usuário selecionado: " + u.name, Toast.LENGTH_SHORT).show();
        finish(); // volta para MainActivity
    }

    private void showEditDialog(User u) {
        final com.google.android.material.textfield.TextInputLayout til =
                new com.google.android.material.textfield.TextInputLayout(this);
        final EditText et = new EditText(this);
        et.setHint("Nome do usuário");
        et.setSingleLine(true);
        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(24)});
        et.setText(u.name);
        et.setSelection(et.getText().length());
        til.addView(et);

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Editar usuário")
                .setView(til)
                .setPositiveButton("Salvar", (d, w) -> {
                    String name = et.getText().toString().trim();
                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(this, "Digite um nome", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        repo.updateUserName(u.id, name);
                        loadUsers();
                        Toast.makeText(this, "Nome atualizado", Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {
                        Toast.makeText(this, "Erro: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    @Override
    public void onUserLongClick(User u) {
        String[] opcoes = {"Editar usuário", "Excluir usuário"};

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle(u.name)
                .setItems(opcoes, (dialog, which) -> {
                    if (which == 0) {
                        // Editar
                        showEditDialog(u);
                    } else if (which == 1) {
                        // Excluir (mesma lógica que você já tinha)
                        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                                .setTitle("Remover usuário?")
                                .setMessage("Excluir \"" + u.name + "\" e suas pontuações?")
                                .setPositiveButton("Excluir", (d2, w2) -> {
                                    repo.deleteById(u.id);
                                    if (SessionPrefs.getCurrentUserId(this) == u.id) {
                                        SessionPrefs.clear(this);
                                    }
                                    loadUsers();
                                })
                                .setNegativeButton("Cancelar", null)
                                .show();
                    }
                })
                .show();
    }

}