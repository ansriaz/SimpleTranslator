package com.ansriaz.mytranslator.entities;

import android.provider.BaseColumns;

/**
 * Created by ansriaz on 04/01/2017.
 */

public class Language implements BaseColumns {
    public static final String TABLE_NAME = "languageCodes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LANGUAGE = "language";
    public static final String COLUMN_ISO_CODE = "iso_code";
    public static final String COLUMN_ISO_CODE2 = "iso_code2";
}