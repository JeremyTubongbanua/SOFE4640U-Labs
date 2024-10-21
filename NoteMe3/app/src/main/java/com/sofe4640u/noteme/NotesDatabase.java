package com.sofe4640u.noteme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class NotesDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "local_database.db";
    private static final String TABLE_NAME = "notes";
    private static final String COL1 = "ID";
    private static final String COL2 = "TITLE";
    private static final String COL3 = "SUBTITLE";
    private static final String COL4 = "CONTENT";
    private static final String COL5 = "COLOUR_NAME";
    private static final String COL6 = "IMAGE_BIN";

    public NotesDatabase(Context context) {
        super(context, DATABASE_NAME, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL2 + " TEXT, " +
                COL3 + " TEXT, " +
                COL4 + " TEXT, " +
                COL5 + " TEXT, " +
                COL6 + " BLOB)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addNote(String title, String subtitle, String content, String colourName, byte[] imageData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, title);
        contentValues.put(COL3, subtitle);
        contentValues.put(COL4, content);
        contentValues.put(COL5, colourName);
        contentValues.put(COL6, imageData);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public boolean updateNote(String id, String title, String subtitle, String content, String colourName, byte[] imageData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, title);
        contentValues.put(COL3, subtitle);
        contentValues.put(COL4, content);
        contentValues.put(COL5, colourName);
        contentValues.put(COL6, imageData);

        int result = db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id});
        return result > 0;
    }

    public Cursor getNotes() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT ID as _id, TITLE, SUBTITLE, CONTENT, COLOUR_NAME, IMAGE_BIN FROM " + TABLE_NAME, null);
    }

    public boolean deleteNote(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, "ID = ?", new String[]{id});
        return result > 0;
    }

    public Cursor getNotesFiltered(String title, NoteColour colorFilter) {
        SQLiteDatabase db = this.getWritableDatabase();
        StringBuilder queryBuilder = new StringBuilder("SELECT ID as _id, TITLE, SUBTITLE, CONTENT, COLOUR_NAME, IMAGE_URI FROM " + TABLE_NAME);
        List<String> selectionArgsList = new ArrayList<>();

        if ((title == null || title.isEmpty()) && colorFilter == null) {
            return getNotes();
        } else {
            queryBuilder.append(" WHERE ");
            boolean firstCondition = true;

            if (title != null && !title.isEmpty()) {
                queryBuilder.append("TITLE LIKE ?");
                selectionArgsList.add("%" + title + "%");
                firstCondition = false;
            }

            if (colorFilter != null) {
                if (!firstCondition) {
                    queryBuilder.append(" AND ");
                }
                queryBuilder.append("COLOUR_NAME = ?");
                selectionArgsList.add(colorFilter.name());
            }
        }
        String[] selectionArgs = selectionArgsList.toArray(new String[0]);
        return db.rawQuery(queryBuilder.toString(), selectionArgs);
    }
}
