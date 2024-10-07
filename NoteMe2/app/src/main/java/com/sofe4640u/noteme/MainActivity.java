package com.sofe4640u.noteme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NotesDatabase notesDatabase;
    private SimpleCursorAdapter adapter;
    private NoteColour currentColorFilter = null; // Keep track of selected color filter

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
        loadNotes(notesListView, "", currentColorFilter);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadNotes(notesListView, query, currentColorFilter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadNotes(notesListView, newText, currentColorFilter);
                return true;
            }
        });

        RecyclerView colorRecyclerView = findViewById(R.id.colorRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        colorRecyclerView.setLayoutManager(layoutManager);

        List<NoteColour> colorList = Arrays.asList(NoteColour.values());

        ColorAdapter colorAdapter = new ColorAdapter(colorList, selectedColor -> {
            Log.d("MainActivity", "Selected Color: " + selectedColor.name());
            currentColorFilter = selectedColor;
            loadNotes(notesListView, searchView.getQuery().toString(), currentColorFilter);
        });

        colorRecyclerView.setAdapter(colorAdapter);

        Button resetBtn = findViewById(R.id.resetBtn);
        resetBtn.setOnClickListener(v -> {
            currentColorFilter = null; // Reset color filter
            searchView.setQuery("", false); // Clear search query
            colorAdapter.resetSelection(); // Reset color selection in adapter
            loadNotes(notesListView, "", currentColorFilter); // Reload notes without filters
        });
    }

    private void loadNotes(ListView notesListView, String filter, NoteColour colorFilter) {
        Cursor cursor = notesDatabase.getNotesFiltered(filter, colorFilter);
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
            String name = String.valueOf(cursor1.getColumnIndex("COLOUR_NAME"));

            String colourName = cursor1.getString(4);
            int color = getColorFromName(colourName);
            RelativeLayout layout = (RelativeLayout) view.getParent();
            layout.setBackgroundColor(color);
            Log.d("column isf", colourName);

            return false;

        });

        notesListView.setAdapter(adapter);
    }

    private int getColorFromName(String colourName) {
        try {
            NoteColour colour = NoteColour.valueOf(colourName);
            return Color.rgb(colour.getR(), colour.getG(), colour.getB());
        } catch (IllegalArgumentException e) {
            return Color.WHITE; // Default color if parsing fails
        }
    }
}
