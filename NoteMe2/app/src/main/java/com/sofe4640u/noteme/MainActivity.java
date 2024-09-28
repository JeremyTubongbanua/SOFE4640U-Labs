package com.sofe4640u.noteme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addNewNoteButton = findViewById(R.id.button);
        addNewNoteButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, NewNote.class));
        });

        loadNotes(findViewById(R.id.notes), new NotesDatabase(this));
    }

    private void loadNotes(ListView notesListView, NotesDatabase notesDatabase) {
        Cursor cursor = notesDatabase.getData();
        String[] fromColumns = { "TITLE", "SUBTITLE" };
        int[] toViews = { android.R.id.text1, android.R.id.text2 };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursor,
                fromColumns,
                toViews,
                0);

        notesListView.setAdapter(adapter);
    }
}
