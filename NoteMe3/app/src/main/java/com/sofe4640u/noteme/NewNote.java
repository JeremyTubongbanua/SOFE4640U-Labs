package com.sofe4640u.noteme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewNote extends AppCompatActivity {

    // intent request codes
    private static final int GET_FROM_GALLERY = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    Uri imageUri = null;

    NotesDatabase databaseHelper;
    EditText titleEditText, subtitleEditText, contentEditText;
    Button deleteBtn, doneBtn, backBtn, uploadBtn, cameraBtn;
    ImageView imageView;
    Spinner colourSpinner;
    String noteId = null; // Track if we're editing a note

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newnote);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_IMAGE_CAPTURE);
        }

        databaseHelper = new NotesDatabase(this);

        titleEditText = findViewById(R.id.titleEditText);
        subtitleEditText = findViewById(R.id.subtitleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        colourSpinner = findViewById(R.id.colourSpinner);
        imageView = findViewById(R.id.imageView);
        uploadBtn = findViewById(R.id.uploadBtn);
        cameraBtn = findViewById(R.id.cameraBtn);

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

            // Retrieve the image binary data from the intent
            byte[] imageBin = intent.getByteArrayExtra("imageBin");

            if (imageBin != null && imageBin.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBin, 0, imageBin.length);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);  // Set the image to the imageView
                    imageUri = saveBitmapToUri(bitmap);  // Save it to the imageUri for further updates
                }
            }

            doneBtn.setOnClickListener(v -> updateNote());
        } else {
            doneBtn.setOnClickListener(v -> saveNote());
        }

        backBtn.setOnClickListener(v -> startActivity(new Intent(NewNote.this, MainActivity.class)));

        deleteBtn.setOnClickListener(v -> {
            if (noteId != null) {
                boolean isDeleted = databaseHelper.deleteNote(noteId);
                if (isDeleted) {
                    Toast.makeText(NewNote.this, "Note deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NewNote.this, "Failed to delete note", Toast.LENGTH_SHORT).show();
                }
                startActivity(new Intent(NewNote.this, MainActivity.class));  // Go back to main activity after deletion
            }
        });

        uploadBtn.setOnClickListener(v -> openGallery());

        cameraBtn.setOnClickListener(v -> openCamera());
    }

    private Uri saveBitmapToUri(Bitmap bitmap) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File imagePath = new File(directory, "image_" + System.currentTimeMillis() + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Uri.fromFile(imagePath);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GET_FROM_GALLERY);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this,
                        "com.sofe4640u.noteme.fileprovider",
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Camera is not available", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GET_FROM_GALLERY && data != null) {
                imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                try {
                    // Use the Uri to get the Bitmap
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    imageView.setImageBitmap(bitmap);  // Display the image in the ImageView
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private byte[] getImageAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private void saveNote() {
        String title = titleEditText.getText().toString();
        String subtitle = subtitleEditText.getText().toString();
        String content = contentEditText.getText().toString();

        byte[] imageData = null;
        if (imageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageData = getImageAsByteArray(bitmap);
            } catch (IOException e) {
                Toast.makeText(NewNote.this, "Image loading error", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (title.isEmpty()) {
            Toast.makeText(NewNote.this, "Title is required", Toast.LENGTH_SHORT).show();
            return;
        }

        NoteColour selectedColour = (NoteColour) colourSpinner.getSelectedItem();
        String colourName = selectedColour.name();

        boolean insertData = databaseHelper.addNote(title, subtitle, content, colourName, imageData);

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

        byte[] imageData = null;
        if (imageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageData = getImageAsByteArray(bitmap);
            } catch (IOException e) {
                Toast.makeText(NewNote.this, "Image loading error", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (title.isEmpty()) {
            Toast.makeText(NewNote.this, "Title is required", Toast.LENGTH_SHORT).show();
            return;
        }

        NoteColour selectedColour = (NoteColour) colourSpinner.getSelectedItem();
        String colourName = selectedColour.name();

        boolean updateData = databaseHelper.updateNote(noteId, title, subtitle, content, colourName, imageData);

        if (updateData) {
            Toast.makeText(NewNote.this, "Note updated successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(NewNote.this, MainActivity.class));
        } else {
            Toast.makeText(NewNote.this, "Failed to update note", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
