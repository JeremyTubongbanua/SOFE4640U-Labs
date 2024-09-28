package com.sofe4640u.noteme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addNewNoteButton = findViewById(R.id.button);

        addNewNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewNote.class);
                startActivity(intent);  // Starts NewNoteActivity
            }
        });

        loadNotes(findViewById(R.id.notes), new NotesDatabase(this));
    }

    private void loadNotes(ListView notesListView, NotesDatabase notesDatabase) {
        Cursor cursor = notesDatabase.getData();
        // Define columns to display in the ListView
        String[] fromColumns = { "TITLE", "SUBTITLE" };
        int[] toViews = { android.R.id.text1, android.R.id.text2 }; // Layout items to map columns

        // Create an adapter to map columns to the UI
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursor,
                fromColumns,
                toViews,
                0);

        // Set adapter to ListView
        notesListView.setAdapter(adapter);
    }
}
