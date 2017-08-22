package com.mad.memome.activity;

/**
 * This class is used to view an event or reminder's details
 */

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mad.memome.R;
import com.mad.memome.database.Datasource;
import com.mad.memome.model.Reminder;

public class ViewActivity extends AppCompatActivity {
   // static final LatLng TutorialsPoint = new LatLng(21 , 57);
   // private GoogleMap googleMap;


    TextView start_time_txt;
    TextView end_time_txt;
    TextView location_txt;
    TextView title_txt;
    MarkActivity ma;
    TextView content_txt;
    ProgressBar mark_pb;
    LinearLayout location_ly;

    static final int REQUEST_AUTHORIZATION = 1001;
    private Reminder reminder;
    private boolean reminderChanged;
    Datasource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_activity);
        location_ly= (LinearLayout) findViewById(R.id.location_ly);
        mark_pb = (ProgressBar) findViewById(R.id.mark_pb);
        ma = new MarkActivity(getApplicationContext());
        start_time_txt = (TextView) findViewById(R.id.start_time);
        end_time_txt = (TextView) findViewById(R.id.end_time);
        location_txt = (TextView) findViewById(R.id.location_txt);
        title_txt = (TextView) findViewById(R.id.title_txt);
        content_txt = (TextView) findViewById(R.id.content_txt);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        datasource = new Datasource(this);
        datasource.open();
        Intent intent = getIntent();
        int mReminderId = intent.getIntExtra(getString(R.string.notification_id), 0);
        reminder = datasource.getNotification(mReminderId);
        assignReminderValues();
        location_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent new_intent = new Intent(ViewActivity.this,MapsActivity.class);
                new_intent.putExtra("Location_value",location_txt.getText());
                startActivity(new_intent );
            }
        });
       /* SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);*/

       /* try {
            if (googleMap == null) {
                ((MapFragment) getFragmentManager().
                        findFragmentById(map)).getMapAsync(this);
            }
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        *//*    Marker TP = googleMap.addMarker(new MarkerOptions().
                    position(TutorialsPoint).title("TutorialsPoint"));*//*
        }
        catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        datasource.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        datasource.open();
    }

    /**
     *  Assign the value of selected reminder or event
     */
    public void assignReminderValues() {
        title_txt.setText(reminder.getTitle());
        location_txt.setText(reminder.getLocation());
        content_txt.setText(reminder.getContent());
        start_time_txt.setText(reminder.getStartDateAndTime());
        end_time_txt.setText(reminder.getEndDateAndTime());
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_viewer, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (reminderChanged) {
            finish();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                datasource.deleteReminder(reminder.getId());
                finish();
                return true;
            case android.R.id.home:
                finish(); // close this activity and return to preview activity (if there is any)
                return super.onOptionsItemSelected(item);
            case R.id.action_mark_as_done:
                new AsyncTask<Integer, Void, Void>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        mark_pb.setVisibility(View.VISIBLE);

                    }


                    @Override
                    protected Void doInBackground(Integer... params) {

                        try {
                            Thread.sleep(500);
                            ma.mark(params[0]);
                        } catch (Exception e) {
                            Toast.makeText(ViewActivity.this, e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        mark_pb.setVisibility(View.GONE);
                    }
                }.execute(reminder.getId());


                return true;

        }
        return super.onOptionsItemSelected(item);
    }


  /*  @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap=googleMap;
        LatLng sydney = new LatLng(-34, 151);
        this.googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }*/
}

