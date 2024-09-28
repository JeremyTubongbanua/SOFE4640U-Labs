package com.sofe4640u.noteme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
                loadNotes(notesListView, query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadNotes(notesListView, newText);
                return true;
            }
        });
    }

    private void loadNotes(ListView notesListView, String filter) {
        Cursor cursor = notesDatabase.getNotesFilteredByTitle(filter);
        String[] fromColumns = { "TITLE", "SUBTITLE" };
        int[] toViews = { R.id.noteTitle, R.id.noteSubtitle };

        adapter = new SimpleCursorAdapter(
                this,
                R.layout.note_list_item,
                cursor,
                fromColumns,
                toViews,
                0);

        adapter.setViewBinder((view, cursor1, columnIndex) -> {
            if (columnIndex == cursor1.getColumnIndex("COLOUR_NAME")) {
                String colourName = cursor1.getString(cursor1.getColumnIndex("COLOUR_NAME"));
                int color = getColorFromName(colourName);
                RelativeLayout layout = (RelativeLayout) view.getParent();
                layout.setBackgroundColor(color);
                return true;
            }
            return false;
        });

        notesListView.setAdapter(adapter);
    }

    private int getColorFromName(String colourName) {
        NoteColour colour = NoteColour.valueOf(colourName);
        return Color.rgb(colour.getR(), colour.getG(), colour.getB());
    }
}
