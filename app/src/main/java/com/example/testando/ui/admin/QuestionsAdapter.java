package com.example.testando.ui.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testando.data.QuestionRepository;
import com.example.testando.data.QuestionRepository.AdminQuestion;
import com.example.testando.databinding.ItemQuestionBinding;

import java.util.ArrayList;
import java.util.List;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.VH> {

    public interface OnItemAction {
        void onEdit(long qid);
        void onDelete(long qid);
    }

    private final OnItemAction listener;
    private final List<AdminQuestion> items = new ArrayList<>();

    public QuestionsAdapter(OnItemAction l) { this.listener = l; }

    public void submit(List<AdminQuestion> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemQuestionBinding b = ItemQuestionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        AdminQuestion q = items.get(pos);
        h.bind(q, listener);
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        private final ItemQuestionBinding b;
        VH(ItemQuestionBinding b) { super(b.getRoot()); this.b = b; }
        void bind(AdminQuestion q, OnItemAction l) {
            b.tvTop.setText(q.topicName + " • " + (q.difficulty == QuestionRepository.D_NORMAL ? "Normal" : "Avançado"));
            b.tvQuestion.setText(q.text);
            b.btnEdit.setOnClickListener(v -> l.onEdit(q.id));
            b.btnDelete.setOnClickListener(v -> l.onDelete(q.id));
        }
    }
}