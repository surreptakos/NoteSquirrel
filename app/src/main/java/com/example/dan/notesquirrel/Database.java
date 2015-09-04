package com.example.dan.notesquirrel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class Database extends SQLiteOpenHelper {
    public Database(Context context) {
        super(context, "note.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //called when you try to use your database, not when the constructor is called
        String sql = "create table POINTS (ID INTEGER PRIMARY KEY, X INTEGER NOT NULL, Y INTEGER NOT NULL";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Not used
    }
}
