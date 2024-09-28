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

        Button backButton = findViewById(R.id.button4);
        Button doneButton = findViewById(R.id.button5);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewNote.this, MainActivity.class);
                startActivity(intent);
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
                Intent intent = new Intent(NewNote.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveNote() {
        String title = titleEditText.getText().toString();
        String subtitle = subtitleEditText.getText().toString();
        String content = contentEditText.getText().toString();

        // Get the selected color from the spinner
        NoteColour selectedColor = NoteColour.valueOf(colourSpinner.getSelectedItem().toString());

        boolean insertData = databaseHelper.addData(title, subtitle, content,
                selectedColor.getR(), selectedColor.getG(), selectedColor.getB());

        if (insertData) {
            Toast.makeText(NewNote.this, "Note saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(NewNote.this, "Failed to save note", Toast.LENGTH_SHORT).show();
        }
    }
}
