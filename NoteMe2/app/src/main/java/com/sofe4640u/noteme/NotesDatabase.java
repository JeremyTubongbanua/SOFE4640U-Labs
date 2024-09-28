package com.sofe4640u.noteme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NotesDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "local_database.db";
    private static final String TABLE_NAME = "notes";
    private static final String COL1 = "ID";
    private static final String COL2 = "TITLE";
    private static final String COL3 = "SUBTITLE";
    private static final String COL4 = "CONTENT";
    private static final String COL5 = "COLOUR_NAME"; // Store the name of the colour

    public NotesDatabase(Context context) {
        super(context, DATABASE_NAME, null, 3); // Updated version number due to schema change
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " TEXT, " +
                COL3 + " TEXT, " +
                COL4 + " TEXT, " +
                COL5 + " TEXT)"; // Store colour as a text column
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addNote(String title, String subtitle, String content, String colourName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, title);
        contentValues.put(COL3, subtitle);
        contentValues.put(COL4, content);
        contentValues.put(COL5, colourName);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1; // returns true if data is inserted successfully
    }

    public Cursor getNotes() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT ID as _id, TITLE, SUBTITLE, CONTENT, COLOUR_NAME FROM " + TABLE_NAME, null);
    }

    public Cursor getNotesFilteredByTitle(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (title == null || title.isEmpty()) {
            return getNotes(); // Return all notes if no filter is applied
        }
        return db.rawQuery("SELECT ID as _id, TITLE, SUBTITLE, CONTENT, COLOUR_NAME FROM " + TABLE_NAME +
                " WHERE TITLE LIKE ?", new String[]{"%" + title + "%"});
    }
}
