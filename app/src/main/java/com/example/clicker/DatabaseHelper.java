package com.example.clicker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "clicker_db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_LEVEL = "level";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTableQuery = "CREATE TABLE " + TABLE_USERS + "(" +
                COLUMN_USERNAME + " TEXT PRIMARY KEY," +
                COLUMN_PASSWORD + " TEXT," +
                COLUMN_LEVEL + " INTEGER DEFAULT 1)";
        db.execSQL(createUserTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?", new String[]{username, password});
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public int getUserLevel(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_LEVEL + " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USERNAME + " = ?", new String[]{username});
        int level = 1;
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(COLUMN_LEVEL);
            if (columnIndex != -1) {
                level = cursor.getInt(columnIndex);
            } else {
                // Handle case when COLUMN_LEVEL is not found
            }
            cursor.close();
        }
        return level;
    }


    public void incrementUserLevel(String username) {
        int currentLevel = getUserLevel(username);
        int newLevel = currentLevel + 1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LEVEL, newLevel);
        db.update(TABLE_USERS, values, COLUMN_USERNAME + " = ?", new String[]{username});
    }
}
