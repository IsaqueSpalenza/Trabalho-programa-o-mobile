package com.example.example.filmes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }

    private final List<Movie> data;
    private final OnItemClickListener listener;

    public MovieAdapter(List<Movie> data, OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = data.get(position);
        holder.bind(movie, listener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }

        void bind(final Movie movie, final OnItemClickListener listener) {
            tvTitle.setText(movie.getTitle());
            itemView.setOnClickListener(v -> listener.onItemClick(movie));
        }
    }
}