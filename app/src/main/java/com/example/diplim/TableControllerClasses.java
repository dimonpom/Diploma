package com.example.diplim;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TableControllerClasses extends DBHandler {
    public TableControllerClasses(Context context) {
        super(context);
    }

    public boolean create(ClassesObject classesObject){
        ContentValues values = new ContentValues();

        values.put("theme", classesObject.theme);
        values.put("subject", classesObject.subject);
        values.put("date", classesObject.date);

        SQLiteDatabase database = this.getWritableDatabase();

        boolean createSuccessful = database.insert("classes",null, values) > 0;

        return createSuccessful;
    }

    public int count(){
        SQLiteDatabase database = this.getWritableDatabase();
        String SQL = "SELECT * FROM classes";
        int recordCount = database.rawQuery(SQL, null).getCount();
        database.close();

        return recordCount;
    }

    public boolean delete(int id){
        boolean deleteSuccessful = false;
        SQLiteDatabase database = this.getWritableDatabase();

        deleteSuccessful = database.delete("classes","id = '"+id+"'",null)>0;
        database.close();

        return deleteSuccessful;
    }

    public List<ClassesObject> read(){
        List<ClassesObject> recordsList = new ArrayList<ClassesObject>();
        String SQL = "Select * From classes Order by id DESC";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(SQL, null);

        if (cursor.moveToFirst()){
            do {
                int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
                String classTheme = cursor.getString(cursor.getColumnIndex("theme"));
                String classSubject = cursor.getString(cursor.getColumnIndex("subject"));
                String classDate = cursor.getString(cursor.getColumnIndex("date"));

                ClassesObject classesObject = new ClassesObject();
                classesObject.id = id;
                classesObject.theme = classTheme;
                classesObject.subject = classSubject;
                classesObject.date = classDate;

                recordsList.add(classesObject);

            }while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return recordsList;
    }

}
