package com.ansriaz.mytranslator.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import com.ansriaz.mytranslator.entities.AutoCompleteText;
import com.ansriaz.mytranslator.entities.Language;

/**
 * Created by ansriaz on 04/01/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Translator.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table " + Language.TABLE_NAME + " (" +
                        Language.COLUMN_ID + " integer primary key, " +
                        Language.COLUMN_LANGUAGE + " text, " +
                        Language.COLUMN_ISO_CODE2 + " text, " +
                        Language.COLUMN_ISO_CODE + " text)"
        );
        db.execSQL(
                "create table " + AutoCompleteText.TABLE_NAME + " (" +
                        AutoCompleteText._ID + " integer primary key, " +
                        AutoCompleteText.COLUMN_TEXT + " text)"
        );
        populateData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + Language.TABLE_NAME + "");
        onCreate(db);
    }

    public void populateData(SQLiteDatabase db){

        db.execSQL("INSERT INTO "+Language.TABLE_NAME
                +"("+Language.COLUMN_LANGUAGE+","+Language.COLUMN_ISO_CODE+","+Language.COLUMN_ISO_CODE2
                +") VALUES('Arabic','ara','ar')");
        db.execSQL("INSERT INTO "+Language.TABLE_NAME+"("+Language.COLUMN_LANGUAGE+","+Language.COLUMN_ISO_CODE+","+Language.COLUMN_ISO_CODE2
                +") VALUES('English','eng','en')");
        db.execSQL("INSERT INTO "+Language.TABLE_NAME+"("+Language.COLUMN_LANGUAGE+","+Language.COLUMN_ISO_CODE+","+Language.COLUMN_ISO_CODE2
                +") VALUES('German','deu','de')");
        db.execSQL("INSERT INTO "+Language.TABLE_NAME+"("+Language.COLUMN_LANGUAGE+","+Language.COLUMN_ISO_CODE+","+Language.COLUMN_ISO_CODE2
                +") VALUES('Italian','ita','it')");
        db.execSQL("INSERT INTO "+Language.TABLE_NAME+"("+Language.COLUMN_LANGUAGE+","+Language.COLUMN_ISO_CODE+","+Language.COLUMN_ISO_CODE2
                +") VALUES('Urdu','urd','ur')");

        db.execSQL("INSERT INTO "+AutoCompleteText.TABLE_NAME+"("+AutoCompleteText.COLUMN_TEXT+") VALUES('canada')");
        db.execSQL("INSERT INTO "+AutoCompleteText.TABLE_NAME+"("+AutoCompleteText.COLUMN_TEXT+") VALUES('computer')");
        db.execSQL("INSERT INTO "+AutoCompleteText.TABLE_NAME+"("+AutoCompleteText.COLUMN_TEXT+") VALUES('dog')");
        db.execSQL("INSERT INTO "+AutoCompleteText.TABLE_NAME+"("+AutoCompleteText.COLUMN_TEXT+") VALUES('love')");
        db.execSQL("INSERT INTO "+AutoCompleteText.TABLE_NAME+"("+AutoCompleteText.COLUMN_TEXT+") VALUES('car')");
        db.execSQL("INSERT INTO "+AutoCompleteText.TABLE_NAME+"("+AutoCompleteText.COLUMN_TEXT+") VALUES('cat')");

    }
}