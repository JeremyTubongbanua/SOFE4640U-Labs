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
    private static final String COL5 = "COLOR_R";
    private static final String COL6 = "COLOR_G";
    private static final String COL7 = "COLOR_B";

    public NotesDatabase(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " TITLE TEXT, SUBTITLE TEXT, CONTENT TEXT, COLOR_R INTEGER, COLOR_G INTEGER, COLOR_B INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String title, String subtitle, String content, int r, int g, int b) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, title);
        contentValues.put(COL3, subtitle);
        contentValues.put(COL4, content);
        contentValues.put(COL5, r);
        contentValues.put(COL6, g);
        contentValues.put(COL7, b);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT ID as _id, TITLE, SUBTITLE, CONTENT, COLOR_R, COLOR_G, COLOR_B FROM " + TABLE_NAME, null);
    }
}
