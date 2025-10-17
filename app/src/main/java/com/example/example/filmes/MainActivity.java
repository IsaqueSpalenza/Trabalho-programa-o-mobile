package com.example.example.filmes;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );

        List<Movie> movies = createSampleMovies();

        MovieAdapter adapter = new MovieAdapter(movies, movie -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("movie_title", movie.getTitle());
            intent.putExtra("movie_score", movie.getScore());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }

    private List<Movie> createSampleMovies() {
        List<Movie> list = new ArrayList<>();

        list.add(new Movie("A Tale of Two Sisters", 7.1));
        list.add(new Movie("The Wailing", 7.4));
        list.add(new Movie("The Thing", 8.2));
        list.add(new Movie("Amadeus", 8.4));
        list.add(new Movie("Cure", 7.5));
        list.add(new Movie("Lake Mungo", 6.2));
        list.add(new Movie("Whiplash", 8.5));
        list.add(new Movie("The Hunt", 8.3));
        list.add(new Movie("The Lord of the Rings: The Return of the King", 9.0));
        list.add(new Movie("The Seventh Seal", 8.1));

        return list;
    }
}