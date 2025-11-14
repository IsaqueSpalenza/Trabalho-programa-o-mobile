package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.atividade9.Nome;
import java.util.ArrayList;
import java.util.List;

public class NomeDao {
    private final AppDatabase helper;

    public NomeDao(Context ctx) { this.helper = new AppDatabase(ctx); }

    public long inserir(String nome) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(AppDatabase.COL_NOME, nome);
        long id = db.insert(AppDatabase.TABELA_NOME, null, cv);
        db.close();
        return id;
    }

    public List<Nome> listarTodos() {
        SQLiteDatabase db = helper.getReadableDatabase();
        List<Nome> lista = new ArrayList<>();
        Cursor c = db.query(AppDatabase.TABELA_NOME,
                new String[]{AppDatabase.COL_ID, AppDatabase.COL_NOME},
                null, null, null, null, AppDatabase.COL_ID + " DESC");
        while (c.moveToNext()) {
            long id = c.getLong(0);
            String nome = c.getString(1);
            lista.add(new Nome(id, nome));
        }
        c.close();
        db.close();
        return lista;
    }

    public int excluir(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int linhas = db.delete(AppDatabase.TABELA_NOME,
                AppDatabase.COL_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
        return linhas;
    }

    // NOVO: Atualizar registro existente
    public int atualizar(Nome nome) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(AppDatabase.COL_NOME, nome.getNome());

        int linhas = db.update(
                AppDatabase.TABELA_NOME,
                cv,
                AppDatabase.COL_ID + "=?",
                new String[]{String.valueOf(nome.getId())}
        );
        db.close();
        return linhas;
    }
}
