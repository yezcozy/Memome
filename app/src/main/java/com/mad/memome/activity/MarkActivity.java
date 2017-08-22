package com.mad.memome.activity;

import android.content.Context;

import com.mad.memome.database.Datasource;

/**
 * This class is used to mark the event
 */

public class MarkActivity {
    Datasource datasource;
    Context context;

    public MarkActivity(Context context) {
        this.context = context;
        datasource = new Datasource(context);
    }

    public void mark(int id) {

        datasource.open();
        datasource.markDone(id);
        datasource.close();
    }


}
