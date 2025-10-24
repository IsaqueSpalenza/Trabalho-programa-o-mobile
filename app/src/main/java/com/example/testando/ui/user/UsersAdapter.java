package com.example.testando.ui.user;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testando.data.User;
import com.example.testando.databinding.ItemUserBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.VH> {

    public interface OnUserClick {
        void onUserClick(User u);
        void onUserLongClick(User u);
    }

    private final OnUserClick listener;
    private final List<User> items = new ArrayList<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public UsersAdapter(OnUserClick l) { this.listener = l; }

    public void submit(List<User> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding b = ItemUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        User u = items.get(pos);
        h.bind(u, listener, sdf);
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        private final ItemUserBinding b;
        VH(ItemUserBinding b) { super(b.getRoot()); this.b = b; }
        void bind(User u, OnUserClick l, SimpleDateFormat sdf) {
            b.tvName.setText(u.name);
            b.tvCreated.setText("Criado em: " + sdf.format(new Date(u.createdAt * 1000)));
            b.getRoot().setOnClickListener(v -> l.onUserClick(u));
            b.getRoot().setOnLongClickListener(v -> { l.onUserLongClick(u); return true; });
        }
    }
}