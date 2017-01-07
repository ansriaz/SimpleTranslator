package com.ansriaz.mytranslator.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ansriaz.mytranslator.entities.Language;

import java.util.ArrayList;

/**
 * Created by ansriaz on 04/01/2017.
 */

public class LanguageDBHelper {

    private DBHelper dbHelper;

    public LanguageDBHelper(Context context){
        dbHelper = new DBHelper(context);
    }

    public boolean insertLanguage (String lang, String code) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Language.COLUMN_LANGUAGE, lang);
        contentValues.put(Language.COLUMN_ISO_CODE, code);
        db.insert(Language.TABLE_NAME, null, contentValues);

        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + Language.TABLE_NAME +
                " where " + Language.COLUMN_ID + "=" + id + "", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, Language.TABLE_NAME);
        return numRows;
    }

    public boolean updateLanguage (Integer id, String lang, String code) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Language.COLUMN_LANGUAGE, lang);
        contentValues.put(Language.COLUMN_ISO_CODE, code);
        db.update(Language.TABLE_NAME, contentValues, Language.COLUMN_ID + " = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteLanguage (Integer id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(Language.TABLE_NAME,
                Language.COLUMN_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllLanguages() {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + Language.TABLE_NAME + "", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(Language.COLUMN_LANGUAGE)));
            res.moveToNext();
        }
        return array_list;
    }

    public String getLanguageCode(int id) {
        Log.i("id",Integer.toString(id));
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                Language.TABLE_NAME,
                new String[]{Language.COLUMN_ISO_CODE},
                Language.COLUMN_ID + "=?",
                new String[]{Integer.toString(id)},
                null,
                null,
                null
        );

//        cursor = db.rawQuery( "select " + Language.COLUMN_ISO_CODE + " from " + Language.TABLE_NAME +
//                " where " + Language.COLUMN_ID + "=" + id + "", null );

        String code = "";

        while(cursor.moveToNext()) {
            code = cursor.getString(
                    cursor.getColumnIndexOrThrow(Language.COLUMN_ISO_CODE));
        }
        return code;
    }

    public int getRecordCount(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String countQuery = "SELECT  * FROM " + Language.TABLE_NAME;
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public String getISO2Name(String str){
        Log.i("id",str);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                Language.TABLE_NAME,
                new String[]{Language.COLUMN_ISO_CODE2},
                Language.COLUMN_ISO_CODE + "=?",
                new String[]{str},
                null,
                null,
                null
        );

        String code = "";

        while(cursor.moveToNext()) {
            code = cursor.getString(
                    cursor.getColumnIndexOrThrow(Language.COLUMN_ISO_CODE2));
        }
        return code;
    }

    public int getLanguageID(String str){
        Log.i("id",str);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                Language.TABLE_NAME,
                new String[]{Language.COLUMN_ID},
                Language.COLUMN_ISO_CODE + "=?",
                new String[]{str},
                null,
                null,
                null
        );

        int code = -1;

        while(cursor.moveToNext()) {
            code = cursor.getInt(
                    cursor.getColumnIndexOrThrow(Language.COLUMN_ID));
        }
        return code;
    }

}
