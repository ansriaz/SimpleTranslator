package com.ansriaz.mytranslator.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ansriaz.mytranslator.entities.AutoCompleteText;

import java.util.ArrayList;

/**
 * Created by ansriaz on 04/01/2017.
 */

public class AutoCompleteTextDBHelpher {

    DBHelper dbHelper;

    public AutoCompleteTextDBHelpher(Context context){
        dbHelper = new DBHelper(context);
    }

    public boolean insertAutoCompleteText(String text) {
        Log.i("TextToInsert",text);

        if(searchTextInDb(text).isEmpty()){
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(AutoCompleteText.COLUMN_TEXT, text);
            db.insert(AutoCompleteText.TABLE_NAME, null, contentValues);
            return true;
        }
        return false;
    }

    public String searchTextInDb(String searchTerm){

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                AutoCompleteText.TABLE_NAME,
                new String[]{AutoCompleteText.COLUMN_TEXT},
                AutoCompleteText.COLUMN_TEXT + "=?",
                new String[]{searchTerm},
                null,
                null,
                null
        );
        String result = "";

//        while(cursor.moveToNext()) {
//            result = cursor.getString(
//                    cursor.getColumnIndexOrThrow(AutoCompleteText.COLUMN_TEXT));
//        }

        if(cursor.moveToFirst())
            result = cursor.getString(cursor.getColumnIndexOrThrow(AutoCompleteText.COLUMN_TEXT));

        return result;
    }

    public ArrayList getAutoCompItemsFromDb(String searchTerm){

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                AutoCompleteText.TABLE_NAME,
                new String[]{AutoCompleteText.COLUMN_TEXT},
                AutoCompleteText.COLUMN_TEXT + "=?",
                new String[]{searchTerm},
                null,
                null,
                null
        );
        ArrayList<String> items = new ArrayList<>(cursor.getCount());

        while(cursor.moveToNext()) {
            items.add(cursor.getString(
                    cursor.getColumnIndexOrThrow(AutoCompleteText.COLUMN_TEXT)));
        }

        return items;
    }

    public ArrayList getAllDataFromDb(){

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                AutoCompleteText.TABLE_NAME,
                new String[]{AutoCompleteText.COLUMN_TEXT},
                null,
                null,
                null,
                null,
                null
        );
        ArrayList<String> items = new ArrayList<>(cursor.getCount());

        while(cursor.moveToNext()) {
            items.add(cursor.getString(
                    cursor.getColumnIndexOrThrow(AutoCompleteText.COLUMN_TEXT)));
        }

        return items;
    }
}
