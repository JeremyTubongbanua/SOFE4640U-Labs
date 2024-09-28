package com.sofe4640u.noteme;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Spinner;

public class NewNote extends AppCompatActivity {

    NotesDatabase databaseHelper;
    EditText titleEditText, subtitleEditText, contentEditText;
    Spinner colourSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newnote);

        databaseHelper = new NotesDatabase(this);

        titleEditText = findViewById(R.id.titleEditText);
        subtitleEditText = findViewById(R.id.subtitleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        colourSpinner = findViewById(R.id.colourSpinner);

        ArrayAdapter<NoteColour> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, NoteColour.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colourSpinner.setAdapter(adapter);

        findViewById(R.id.button4).setOnClickListener(v -> {
            startActivity(new Intent(NewNote.this, MainActivity.class));
        });

        findViewById(R.id.button5).setOnClickListener(v -> saveNote());
    }

    private void saveNote() {
        String title = titleEditText.getText().toString();
        String subtitle = subtitleEditText.getText().toString();
        String content = contentEditText.getText().toString();

        if (title.isEmpty()) {
            Toast.makeText(NewNote.this, "Title is required", Toast.LENGTH_SHORT).show();
            return;
        }

        NoteColour selectedColor = (NoteColour) colourSpinner.getSelectedItem();

        boolean insertData = databaseHelper.addData(title, subtitle, content,
                selectedColor.getR(), selectedColor.getG(), selectedColor.getB());

        if (insertData) {
            Toast.makeText(NewNote.this, "Note saved successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(NewNote.this, MainActivity.class));
        } else {
            Toast.makeText(NewNote.this, "Failed to save note", Toast.LENGTH_SHORT).show();
        }
    }
}
