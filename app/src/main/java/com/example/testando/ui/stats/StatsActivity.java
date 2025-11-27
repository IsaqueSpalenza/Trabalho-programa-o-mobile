package com.example.testando.ui.stats;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testando.R;
import com.example.testando.data.DatabaseHelper;
import com.example.testando.data.QuestionRepository;
import com.example.testando.data.ScoreRepository;
import com.example.testando.data.SessionPrefs;

import java.util.Locale;
import java.util.Map;

public class StatsActivity extends AppCompatActivity {

    // views do modo novo (com filtros)
    private Spinner spTopic, spDiff;
    private LinearLayout listStats;

    // view do modo legado (sem filtros)
    private LinearLayout containerLegacy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stats);

        // tenta achar as views do layout novo
        spTopic = findViewById(R.id.spTopic);
        spDiff  = findViewById(R.id.spDiff);
        listStats = findViewById(R.id.listStats);

        // se não existir 'spTopic' no layout = modo legado
        if (spTopic == null || spDiff == null || listStats == null) {
            runLegacyMode();
            return;
        }

        // modo novo (com filtros)
        spTopic.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Todos", "Historia", "Matematica"}));

        spDiff.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Todas", "Normal", "Avançado"}));

        Button btnAplicar = findViewById(R.id.btnAplicar);
        btnAplicar.setOnClickListener(v -> loadStats());

        loadStats();
    }

    // modo legado: não há spinners no layout. Usa o ScoreRepository antigo.
    private void runLegacyMode() {
        containerLegacy = findViewById(R.id.containerStats);
        if (containerLegacy == null) return; // layout inválido

        long uid = SessionPrefs.getCurrentUserId(this);
        Map<String, int[]> map = new ScoreRepository(this).getTotalsByTopic(uid);

        containerLegacy.removeAllViews();

        if (map.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("Sem resultados ainda.");
            tv.setTextSize(16f);
            containerLegacy.addView(tv);
            return;
        }

        for (Map.Entry<String, int[]> e : map.entrySet()) {
            String topic = e.getKey();
            int[] vals = e.getValue(); // [sumCorrect, sumTotal]
            int correct = vals[0];
            int total = vals[1];
            float pct = total > 0 ? (100f * correct / total) : 0f;

            TextView tv = new TextView(this);
            tv.setText(String.format(Locale.getDefault(),
                    "%s — %d/%d (%.1f%%)", topic, correct, total, pct));
            tv.setTextSize(16f);
            tv.setPadding(8, 12, 8, 12);
            containerLegacy.addView(tv);
        }
    }

    // modo novo: carrega agregados aplicando filtros de tema e dificuldade.
    private void loadStats() {
        listStats.removeAllViews();

        String topicSel = (String) spTopic.getSelectedItem();
        String topic = "Todos".equals(topicSel) ? null : topicSel;

        String diffSel = (String) spDiff.getSelectedItem();
        Integer diff = null;
        if ("Normal".equals(diffSel)) diff = QuestionRepository.D_NORMAL;
        else if ("Avançado".equals(diffSel)) diff = QuestionRepository.D_AVANCADO;

        long uid = SessionPrefs.getCurrentUserId(this);
        DatabaseHelper dbh = new DatabaseHelper(this);
        SQLiteDatabase db = dbh.getReadableDatabase();

        // WHERE dinâmico
        StringBuilder where = new StringBuilder(DatabaseHelper.C_SCORE_USER_ID + "=?");
        java.util.ArrayList<String> args = new java.util.ArrayList<>();
        args.add(String.valueOf(uid));

        if (topic != null) {
            where.append(" AND ").append(DatabaseHelper.C_SCORE_TOPIC).append("=?");
            args.add(topic);
        }
        if (diff != null) {
            where.append(" AND ").append(DatabaseHelper.C_SCORE_DIFFICULTY).append("=?");
            args.add(String.valueOf(diff));
        }

        String sql =
                "SELECT " +
                        DatabaseHelper.C_SCORE_TOPIC + " AS t, " +
                        DatabaseHelper.C_SCORE_DIFFICULTY + " AS d, " +
                        "SUM(" + DatabaseHelper.C_SCORE_CORRECT + ") AS sum_correct, " +
                        "SUM(" + DatabaseHelper.C_SCORE_TOTAL + ")   AS sum_total, " +
                        "COUNT(*) AS attempts, " +
                        "MAX(" + DatabaseHelper.C_SCORE_TS + ") AS last_ts " +
                        "FROM " + DatabaseHelper.T_SCORES + " " +
                        "WHERE " + where +
                        " GROUP BY t, d " +
                        "ORDER BY t ASC, d ASC";

        try (Cursor c = db.rawQuery(sql, args.toArray(new String[0]))) {
            if (c.moveToFirst()) {
                do {
                    String t = c.getString(c.getColumnIndexOrThrow("t"));
                    int d    = c.getInt(c.getColumnIndexOrThrow("d"));
                    int sumC = c.getInt(c.getColumnIndexOrThrow("sum_correct"));
                    int sumT = c.getInt(c.getColumnIndexOrThrow("sum_total"));
                    int att  = c.getInt(c.getColumnIndexOrThrow("attempts"));
                    addRow(t, d == QuestionRepository.D_NORMAL ? "Normal" : "Avançado",
                            sumC, sumT, att);
                } while (c.moveToNext());
            } else {
                addEmpty();
            }
        }
    }

    private void addRow(String topic, String diffLabel, int correct, int total, int attempts) {
        TextView tv = new TextView(this);
        tv.setText(
                topic + " • " + diffLabel + " — " +
                        correct + "/" + total + " (" + pct(correct, total) + ")  • tentativas: " + attempts
        );
        tv.setTextSize(16f);
        tv.setPadding(0, 12, 0, 12);
        listStats.addView(tv);
    }

    private String pct(int c, int t) {
        if (t <= 0) return "0%";
        float p = (100f * c) / t;
        return String.format(Locale.getDefault(), "%.1f%%", p);
    }

    private void addEmpty() {
        TextView tv = new TextView(this);
        tv.setText("Sem dados para os filtros selecionados.");
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setPadding(0, 24, 0, 24);
        listStats.addView(tv);
    }
}