package com.sofe4640u.noteme;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class NewNote extends AppCompatActivity {

    NotesDatabase databaseHelper;
    EditText titleEditText, subtitleEditText, contentEditText;
    Button deleteBtn, doneBtn, backBtn, imageBtn;
    Spinner colourSpinner;
    String noteId = null; // Track if we're editing a note

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newnote);

        databaseHelper = new NotesDatabase(this);

        titleEditText = findViewById(R.id.titleEditText);
        subtitleEditText = findViewById(R.id.subtitleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        colourSpinner = findViewById(R.id.colourSpinner);

        deleteBtn = findViewById(R.id.deleteBtn);
        backBtn = findViewById(R.id.backBtn);
        doneBtn = findViewById(R.id.doneBtn);

        ArrayAdapter<NoteColour> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, NoteColour.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colourSpinner.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent.hasExtra("noteId")) {
            noteId = intent.getStringExtra("noteId");
            titleEditText.setText(intent.getStringExtra("title"));
            subtitleEditText.setText(intent.getStringExtra("subtitle"));
            contentEditText.setText(intent.getStringExtra("content"));

            String colourName = intent.getStringExtra("colourName");
            NoteColour selectedColour = NoteColour.valueOf(colourName);
            colourSpinner.setSelection(adapter.getPosition(selectedColour));

            doneBtn.setOnClickListener(v -> updateNote());
        } else {
            findViewById(R.id.doneBtn).setOnClickListener(v -> saveNote());
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewNote.this, MainActivity.class));
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noteId != null) {
                    boolean isDeleted = databaseHelper.deleteNote(noteId);
                    if (isDeleted) {
                        Toast.makeText(NewNote.this, "Note deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NewNote.this, "Failed to delete note", Toast.LENGTH_SHORT).show();
                    }
                    startActivity(new Intent(NewNote.this, MainActivity.class)); // Go back to main activity after deletion
                }
            }
        });

        imageBtn.setOnClickListener();
    }

    private void saveNote() {
        String title = titleEditText.getText().toString();
        String subtitle = subtitleEditText.getText().toString();
        String content = contentEditText.getText().toString();

        if (title.isEmpty()) {
            Toast.makeText(NewNote.this, "Title is required", Toast.LENGTH_SHORT).show();
            return;
        }

        NoteColour selectedColour = (NoteColour) colourSpinner.getSelectedItem();
        String colourName = selectedColour.name();

        boolean insertData = databaseHelper.addNote(title, subtitle, content, colourName);

        if (insertData) {
            Toast.makeText(NewNote.this, "Note saved successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(NewNote.this, MainActivity.class));
        } else {
            Toast.makeText(NewNote.this, "Failed to save note", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateNote() {
        String title = titleEditText.getText().toString();
        String subtitle = subtitleEditText.getText().toString();
        String content = contentEditText.getText().toString();

        if (title.isEmpty()) {
            Toast.makeText(NewNote.this, "Title is required", Toast.LENGTH_SHORT).show();
            return;
        }

        NoteColour selectedColour = (NoteColour) colourSpinner.getSelectedItem();
        String colourName = selectedColour.name();

        boolean updateData = databaseHelper.updateNote(noteId, title, subtitle, content, colourName);

        if (updateData) {
            Toast.makeText(NewNote.this, "Note updated successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(NewNote.this, MainActivity.class));
        } else {
            Toast.makeText(NewNote.this, "Failed to update note", Toast.LENGTH_SHORT).show();
        }
    }
}
