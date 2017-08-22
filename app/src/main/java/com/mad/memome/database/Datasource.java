package com.mad.memome.database;

/**
 * Created by Yijun Gai
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mad.memome.model.Reminder;

import java.util.ArrayList;

import static com.mad.memome.database.DatabaseHelper.COL_CONTENT;
import static com.mad.memome.database.DatabaseHelper.COL_END_DATE_AND_TIME;
import static com.mad.memome.database.DatabaseHelper.COL_ID;
import static com.mad.memome.database.DatabaseHelper.COL_LOCATION;
import static com.mad.memome.database.DatabaseHelper.COL_START_DATE_AND_TIME;
import static com.mad.memome.database.DatabaseHelper.COL_STATUS;
import static com.mad.memome.database.DatabaseHelper.COL_TITLE;
import static com.mad.memome.database.DatabaseHelper.NOTIFICATION_TABLE;

/**
 * Manage between database and activity
 */
public class Datasource {
    public static final String LOGTAG = "MAD";
    public static final String DONE = "Done";
    public static final String TITLE = "By title";
    public static final String LOCATION = "By location";
    public static final String START = "By start time";
    public static final String STATUS = "By status";
    SQLiteOpenHelper dbhelper;
    SQLiteDatabase database;
    private static final String[] sAllcolumns = {
            COL_ID ,
            COL_CONTENT,
            COL_TITLE,
            COL_START_DATE_AND_TIME,
            COL_END_DATE_AND_TIME,
            COL_LOCATION,
            COL_STATUS
    };

    /**
     * constructor
     *
     * @param context
     */
    public Datasource(Context context) {
        dbhelper = new DatabaseHelper(context);
    }

    /**
     * open the database
     */
    public void open() {
        database = dbhelper.getWritableDatabase();
    }

    /**
     * close the database
     */
    public void close() {
        dbhelper.close();
    }

    public Reminder create(Reminder reminder) {
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, reminder.getTitle());
        values.put(COL_CONTENT, reminder.getContent());
        values.put(COL_START_DATE_AND_TIME, reminder.getStartDateAndTime());
        values.put(COL_END_DATE_AND_TIME, reminder.getEndDateAndTime());
        values.put(COL_LOCATION, reminder.getLocation());
        values.put(COL_STATUS, reminder.getStatus());
        database.insert(DatabaseHelper.NOTIFICATION_TABLE, null, values);


        return reminder;
    }

    /**
     * delete all records in database
     */
    public void clearAll() {
        database.delete(NOTIFICATION_TABLE, null, null);
    }

    /**
     * mark the event from database
     */
    public void markDone(int id) {
        ContentValues args = new ContentValues();
        args.put(COL_STATUS, DONE);
        database.update(NOTIFICATION_TABLE, args, COL_ID + "=" + id, null);
    }

    /**
     * fetch notification by id
     * @param id
     * @return
     */
    public Reminder getNotification(int id) {

        Cursor cursor = database.rawQuery("SELECT * FROM " + NOTIFICATION_TABLE + " WHERE " + COL_ID + " = ? LIMIT 1", new String[]{String.valueOf(id)});
        Log.d("UU",id+"");
        cursor.moveToFirst();
        Reminder reminder = new Reminder();
        reminder.setId(id);
        reminder.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
        reminder.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT)));
        reminder.setStartDateAndTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_START_DATE_AND_TIME)));
        reminder.setEndDateAndTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_END_DATE_AND_TIME)));
        reminder.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)));
        reminder.setStatus(cursor.getColumnName(cursor.getColumnIndexOrThrow(COL_STATUS)));
        return reminder;
    }

    /**
     * filter reminders by input string
     * @param title
     * @return
     */
    public ArrayList<Reminder> filterReminder(String title) {
        ArrayList<Reminder> reminderList = new ArrayList<>();
        String query;
        query = "SELECT * FROM " + NOTIFICATION_TABLE + " WHERE " + COL_TITLE + " LIKE " + "'%" + title + "%'";


        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Reminder reminder = new Reminder();
                reminder.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                reminder.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
                reminder.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT)));
                reminder.setStartDateAndTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_START_DATE_AND_TIME)));
                reminder.setEndDateAndTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_END_DATE_AND_TIME)));
                reminder.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)));
                reminder.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)));
                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reminderList;


    }

    /**
     * get the id of last notification
     * @return
     */
    public int getLastNotificationId() {
        int data = 0;
        Cursor cursor = database.rawQuery("SELECT " + COL_ID + " FROM " + NOTIFICATION_TABLE + " ORDER BY " + COL_ID + " DESC LIMIT 1", null);
        if (cursor != null && cursor.moveToFirst()) {
            data = cursor.getInt(0);
            cursor.close();
        }
        return data;
    }

    /**
     * fetch all reminders in database
     * @return
     */
    public ArrayList<Reminder> findAll() {

        ArrayList<Reminder> reminderList = new ArrayList<>();
        String query;


        query = "SELECT * FROM " + NOTIFICATION_TABLE;


        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Reminder reminder = new Reminder();
                reminder.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                reminder.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
                reminder.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT)));
                reminder.setStartDateAndTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_START_DATE_AND_TIME)));
                reminder.setEndDateAndTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_END_DATE_AND_TIME)));
                reminder.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)));
                reminder.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)));

                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reminderList;

    }

    /**
     * delete reminder by its id
     * @param reminderid
     */
    public void deleteReminder(int reminderid) {
        database.delete(NOTIFICATION_TABLE, COL_ID + " = ?", new String[]{String.valueOf(reminderid)});
    }
   public void deletelastReminders(ArrayList<Reminder> list){


   }



    /**
     * filter reminders by spinner selected string
     * @param filter
     * @return
     */
    public ArrayList<Reminder> filterAll(String filter) {

        ArrayList<Reminder> reminderList = new ArrayList<>();
        String query;
        String col_name = COL_START_DATE_AND_TIME;
        switch (filter) {
            case TITLE:
                col_name = COL_TITLE;
                break;
            case LOCATION:
                col_name = COL_LOCATION;
                break;
            case START:
                col_name = "strftime " + "(" + COL_START_DATE_AND_TIME + ")";
                break;
            case STATUS:
                col_name = COL_STATUS;
                break;
            default:
                break;
        }

        query = "SELECT * FROM " + NOTIFICATION_TABLE + " ORDER BY " + " upper(" + col_name + ")";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Reminder reminder = new Reminder();
                reminder.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                reminder.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
                reminder.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT)));
                reminder.setStartDateAndTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_START_DATE_AND_TIME)));
                reminder.setEndDateAndTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_END_DATE_AND_TIME)));
                reminder.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)));
                reminder.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)));
                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reminderList;

    }


}