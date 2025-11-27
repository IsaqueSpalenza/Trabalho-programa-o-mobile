package com.example.testando.ui.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testando.R;
import com.example.testando.data.QuestionRepository;

public class EditQuestionActivity extends AppCompatActivity {

    private QuestionRepository repo;
    private Spinner spTopic, spDiff;
    private EditText etQuestion, etA, etB, etC, etD;
    private RadioGroup rgCorrect;
    private long qid = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_question);

        repo = new QuestionRepository(this);

        spTopic = findViewById(R.id.spTopic);
        spDiff  = findViewById(R.id.spDiff);
        etQuestion = findViewById(R.id.etQuestion);
        etA = findViewById(R.id.etA);
        etB = findViewById(R.id.etB);
        etC = findViewById(R.id.etC);
        etD = findViewById(R.id.etD);
        rgCorrect = findViewById(R.id.rgCorrect);

        spTopic.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Historia", "Matematica"}));

        spDiff.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Normal", "Avançado"}));


        qid = getIntent().getLongExtra("QID", -1);
        if (qid > 0) {
            QuestionRepository.AdminQuestion aq = repo.getAdminById(qid);
            if (aq != null) {
                spTopic.setSelection("Historia".equals(aq.topicName) ? 0 : 1);
                spDiff.setSelection(aq.difficulty == QuestionRepository.D_NORMAL ? 0 : 1);
                etQuestion.setText(aq.text);
                etA.setText(aq.options[0]);
                etB.setText(aq.options[1]);
                etC.setText(aq.options[2]);
                etD.setText(aq.options[3]);

                int checkedId;
                switch (aq.correctIndex) {
                    case 0:
                        checkedId = R.id.rbA;
                        break;
                    case 1:
                        checkedId = R.id.rbB;
                        break;
                    case 2:
                        checkedId = R.id.rbC;
                        break;
                    default:
                        checkedId = R.id.rbD;
                        break;
                }
                rgCorrect.check(checkedId);
            }
        } else {
            rgCorrect.check(R.id.rbA); // default
        }

        findViewById(R.id.btnSalvar).setOnClickListener(v -> salvar());
        findViewById(R.id.btnCancelar).setOnClickListener(v -> finish());
    }

    private void salvar() {
        String topicName = (String) spTopic.getSelectedItem();
        int difficulty = spDiff.getSelectedItemPosition() == 0
                ? QuestionRepository.D_NORMAL
                : QuestionRepository.D_AVANCADO;

        String enunciado = etQuestion.getText().toString().trim();
        String a = etA.getText().toString().trim();
        String b = etB.getText().toString().trim();
        String c = etC.getText().toString().trim();
        String d = etD.getText().toString().trim();

        if (TextUtils.isEmpty(enunciado) || TextUtils.isEmpty(a) || TextUtils.isEmpty(b)
                || TextUtils.isEmpty(c) || TextUtils.isEmpty(d)) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int correctIndex;
        int checkedRadioId = rgCorrect.getCheckedRadioButtonId();

        if (checkedRadioId == R.id.rbA) {
            correctIndex = 0;
        } else if (checkedRadioId == R.id.rbB) {
            correctIndex = 1;
        } else if (checkedRadioId == R.id.rbC) {
            correctIndex = 2;
        } else {
            correctIndex = 3;
        }


        long topicId = repo.upsertTopic(topicName);
        String[] options = new String[]{a, b, c, d};

        if (qid > 0) {
            repo.updateQuestionWithOptions(qid, topicId, enunciado, options, correctIndex, difficulty);
            Toast.makeText(this, "Pergunta atualizada!", Toast.LENGTH_SHORT).show();
        } else {
            repo.insertQuestionWithOptions(topicId, enunciado, options, correctIndex, difficulty);
            Toast.makeText(this, "Pergunta adicionada!", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
