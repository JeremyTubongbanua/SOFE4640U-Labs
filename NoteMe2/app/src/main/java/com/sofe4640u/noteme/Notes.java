package com.sofe4640u.noteme;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.database.Cursor;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Notes extends AppCompatActivity {

    NotesDatabase databaseHelper;
    ListView notesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        databaseHelper = new NotesDatabase(this);
        notesListView = findViewById(R.id.notesListView);

        loadNotes();
    }

    private void loadNotes() {
        Cursor cursor = databaseHelper.getData();
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
