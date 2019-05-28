package com.example.diplim;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DB_ver = 1;
    protected static final String DB_name = "ClassesDB";
    public DBHandler(Context context) {
        super(context, DB_name, null, DB_ver);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL = "Create Table classes ( id Integer Primary Key Autoincrement, theme Text, subject Text, date TEXT )";
        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL = "DROP TABLE IF EXISTS classes";
        db.execSQL(SQL);
        onCreate(db);
    }
}
