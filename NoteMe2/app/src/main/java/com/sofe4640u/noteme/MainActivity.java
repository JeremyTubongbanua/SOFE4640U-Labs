package com.sofe4640u.noteme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

public class MainActivity extends AppCompatActivity {

    private NotesDatabase notesDatabase;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notesDatabase = new NotesDatabase(this);

        Button addNewNoteButton = findViewById(R.id.button);
        addNewNoteButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, NewNote.class));
        });

        ListView notesListView = findViewById(R.id.notes);
        loadNotes(notesListView, "");

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform the final search action here
                loadNotes(notesListView, query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the notes as user types
                loadNotes(notesListView, newText);
                return true;
            }
        });
    }

    private void loadNotes(ListView notesListView, String filter) {
        Cursor cursor = notesDatabase.getFilteredData(filter);
        String[] fromColumns = { "TITLE", "SUBTITLE" };
        int[] toViews = { android.R.id.text1, android.R.id.text2 };

        adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursor,
                fromColumns,
                toViews,
                0);

        notesListView.setAdapter(adapter);
    }
}
