package com.example.testando.ui.stats;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testando.R;
import com.example.testando.data.ScoreRepository;
import com.example.testando.data.SessionPrefs;

import java.util.Locale;
import java.util.Map;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stats);

        LinearLayout container = findViewById(R.id.containerStats);
        long uid = SessionPrefs.getCurrentUserId(this);
        Map<String, int[]> map = new ScoreRepository(this).getTotalsByTopic(uid);

        container.removeAllViews();

        if (map.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("Sem resultados ainda.");
            tv.setTextSize(16f);
            container.addView(tv);
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
            container.addView(tv);
        }
    }
}