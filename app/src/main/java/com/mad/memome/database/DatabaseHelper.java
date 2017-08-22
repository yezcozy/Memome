package com.mad.memome.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database class
 */

public class DatabaseHelper extends SQLiteOpenHelper {


    public static DatabaseHelper instance;
    public Context context;

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "MEMOME_DB";

    public static final String NOTIFICATION_TABLE = "NOTIFICATIONS";
    public static final String COL_ID = "ID";
    public static final String COL_TITLE = "TITLE";
    public static final String COL_CONTENT = "CONTENT";
    public static final String COL_START_DATE_AND_TIME = "START_DATE_AND_TIME";
    public static final String COL_END_DATE_AND_TIME = "END_DATE_AND_TIME";
    public static final String COL_LOCATION = "LOCATION";
    public static final String COL_STATUS = "STATUS";

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Constructor
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void onCreate(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + NOTIFICATION_TABLE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " text not null, "
                + COL_CONTENT + " text not null, "
                + COL_LOCATION + " text not null, "
                + COL_START_DATE_AND_TIME + " INTEGER, "
                + COL_END_DATE_AND_TIME + " INTEGER, "
                + COL_STATUS + " text not null)");


    }

    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }


}