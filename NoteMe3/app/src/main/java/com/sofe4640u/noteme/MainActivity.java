package com.sofe4640u.noteme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int GET_FROM_GALLERY = 1;

    private NotesDatabase notesDatabase;
    private SimpleCursorAdapter adapter;
    private NoteColour currentColorFilter = null; // Keep track of selected color filter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, GET_FROM_GALLERY);
        }


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
        String[] fromColumns = { "TITLE", "SUBTITLE", "CONTENT" };
        int[] toViews = { R.id.noteTitle, R.id.noteSubtitle, R.id.noteContent };

        adapter = new SimpleCursorAdapter(
                this,
                R.layout.note_list_item,
                cursor,
                fromColumns,
                toViews,
                0);

        adapter.setViewBinder((view, cursor1, columnIndex) -> {
            try {
                String colourName = cursor1.getString(4);
                int color = getColorFromName(colourName);

                RelativeLayout layout = (RelativeLayout) view.getParent();
                layout.setBackgroundColor(color);

                ImageView imageView = layout.findViewById(R.id.imageView);
                if (imageView != null) {
                    byte[] imageData = cursor1.getBlob(5);

                    if (imageData != null && imageData.length > 0) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        } else {
                            imageView.setImageResource(R.drawable.ic_launcher_background);
                        }
                    } else {
                        imageView.setImageResource(R.drawable.ic_launcher_background);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });


        notesListView.setAdapter(adapter);

        notesListView.setOnItemClickListener((parent, view, position, id) -> {
            Cursor selectedNoteCursor = (Cursor) parent.getItemAtPosition(position);
            String noteId = selectedNoteCursor.getString(selectedNoteCursor.getColumnIndexOrThrow("_id"));
            String title = selectedNoteCursor.getString(selectedNoteCursor.getColumnIndexOrThrow("TITLE"));
            String subtitle = selectedNoteCursor.getString(selectedNoteCursor.getColumnIndexOrThrow("SUBTITLE"));
            String content = selectedNoteCursor.getString(selectedNoteCursor.getColumnIndexOrThrow("CONTENT"));
            String colourName = selectedNoteCursor.getString(selectedNoteCursor.getColumnIndexOrThrow("COLOUR_NAME"));
            byte[] imageBin = selectedNoteCursor.getBlob(selectedNoteCursor.getColumnIndexOrThrow("IMAGE_BIN"));

            Intent intent = new Intent(MainActivity.this, NewNote.class);
            intent.putExtra("noteId", noteId);
            intent.putExtra("title", title);
            intent.putExtra("subtitle", subtitle);
            intent.putExtra("content", content);
            intent.putExtra("colourName", colourName);
            intent.putExtra("imageBin", imageBin);
            startActivity(intent);
        });
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
