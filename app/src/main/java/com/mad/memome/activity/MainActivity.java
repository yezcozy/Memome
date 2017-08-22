package com.mad.memome.activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.mad.memome.R;
import com.mad.memome.adapter.ReminderAdapter;
import com.mad.memome.database.Datasource;
import com.mad.memome.model.Reminder;
import com.mad.memome.receivers.AlarmReceiver;
import com.mad.memome.utils.AlarmUtil;
import com.mad.memome.utils.DateAndTimeUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static com.mad.memome.R.id.fab;

/**
* This is a events management app
 */
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private ArrayList<Reminder> mReminderList = new ArrayList<>();
    private ArrayList<Reminder> mAddReminder = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private ReminderAdapter mAdapter;
    private final static int sZero = 0;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    public static final String MYPREFERENCES = "MyPrefs";
    public static final String MYFILTER_PREFERENCES = "MyPrefs";
    private int id;
    static final String MAD= "MAD";
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private boolean fabb=false;
   // private ProgressBar pb;
    Datasource datasource;
    GoogleAccountCredential mCredential;
    private String filter;
    Spinner spinner;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(MAD, getString(R.string.on_create));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
  //      pb = (ProgressBar) findViewById(R.id.mark_pb_main);
        spinner = (Spinner) findViewById(R.id.spinner_filter);
        String[] filters = new String[]{
                getString(R.string.by_title),
                getString(R.string.by_location),
                getString(R.string.by_start_time),
                getString(R.string.by_status),
                getString(R.string.sort_event)
        };
        final List<String> plantsList = new ArrayList<>(Arrays.asList(filters));
        final int listsize = plantsList.size() - 1;
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.filter_txt, plantsList) {
            @Override
            public int getCount() {
                return (listsize); // Truncate the list
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                ((TextView) view).setTextColor(
                        getResources().getColorStateList(R.color.White)
                );
                TextView tv = (TextView) view;
                if (position % 2 == 1) {
                    // Set the item background color
                    tv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    // Set the alternate item background color
                    tv.setBackgroundColor(getResources().getColor(R.color.colorPink));
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(listsize);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selected_item = spinner.getSelectedItem().toString();
                datasource.open();
                mReminderList.clear();
                mReminderList.addAll(datasource.filterAll(selected_item));
                mAdapter.notifyDataSetChanged();
                datasource.close();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
        datasource = new Datasource(this);
        datasource.open();

        mReminderList.clear();
        mReminderList = datasource.findAll();

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datasource.open();
                Intent intent = new Intent();
                intent.putExtra(getString(R.string.notification_id),datasource.getLastNotificationId()+1);
                intent.setClass(MainActivity.this, AddReminder.class);
                startActivityForResult(intent, sZero);
                datasource.close();

            }
        });



        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new ReminderAdapter(this, mReminderList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                getResultsFromApi();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public void fetchTimelineAsync() {

        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.

/*
        client.getHomeTimeline(0, new JsonHttpResponseHandler() {


            public void onFailure(Throwable e) {
                Log.d("DEBUG", "Fetch timeline error: " + e.toString());
            }
        });
*/

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(MAD, getString(R.string.on_start));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences filter_prefs = getSharedPreferences(MYFILTER_PREFERENCES, MODE_PRIVATE);
        String fil = filter_prefs.getString(getString(R.string.filter), getString(R.string.nu));
        if (fil.equals(getString(R.string.nu))) {
            SharedPreferences prefs = getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);
            String pre = prefs.getString(getString(R.string.spinner),  getString(R.string.by_title));//"By title" is the default value.
            datasource.open();
            mReminderList.clear();
            mReminderList.addAll(datasource.filterAll(pre));
            mAdapter.notifyDataSetChanged();
            datasource.close();
            Log.i(MAD, getString(R.string.on_restart));
        } else {
            datasource.open();
            mReminderList.clear();
            mReminderList.addAll(datasource.filterReminder(fil));
            mAdapter.notifyDataSetChanged();
            datasource.close();
        }
        filter = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(MAD, getString(R.string.on_destroy));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(MAD, getString(R.string.on_pause));
        datasource.close();
        SharedPreferences sharedpreferences = getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String text = spinner.getSelectedItem().toString();
        editor.putString(getString(R.string.spinner), text);
        editor.commit();

        SharedPreferences filter_sharedpreferences = getSharedPreferences(MYFILTER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor filter_editor = filter_sharedpreferences.edit();
        filter_editor.putString(getString(R.string.filter), filter);
        filter_editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(MAD, getString(R.string.on_resume));
        datasource.open();
        mAdapter.notifyDataSetChanged();
        datasource.close();
    }

    /**
     *  Change the text colour in searchview
     * @param view
     */
    private void changeSearchViewTextColor(View view) {
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(Color.WHITE);
                return;
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        changeSearchViewTextColor(searchView);
        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        datasource.open();
                        mReminderList.clear();
                        mReminderList.addAll(datasource.findAll());
                        mAdapter.notifyDataSetChanged();
                        datasource.close();
                        return true;
                    }
                });
        return true;
    }

    /**
     * get access to googleplay service
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * choose user's google account
     */
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.service_hint),
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * check the networking availability
     * @return
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {


            Toast.makeText(getApplicationContext(), R.string.no_network,
                    Toast.LENGTH_SHORT).show();
        } else {
            new MainActivity.MakeRequestTask(mCredential).execute();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filter = newText;
        datasource.open();
        mReminderList.clear();
        mReminderList.addAll(datasource.filterReminder(newText));
        mAdapter.notifyDataSetChanged();
        datasource.close();
        return false;
    }

    /**
     * build google client and connect to google calendar api service
      */
    private class MakeRequestTask extends AsyncTask<Void, Void, ArrayList<Reminder>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;


        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(getString(R.string.Google_api_test))
                    .build();
        }


        @Override
        protected ArrayList<Reminder> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;

        }

        /**
         * get all events from google calendar
         * @return
         * @throws IOException
         */
        private ArrayList<Reminder> getDataFromApi() throws IOException {
            ArrayList<Reminder> raw_remindersList = new ArrayList<Reminder>();
            ArrayList<Reminder> remindersList = new ArrayList<Reminder>();
            DateTime now = new DateTime(System.currentTimeMillis());
            Events events = mService.events().list(getString(R.string.primary))
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setSingleEvents(true)
                    .setOrderBy(getString(R.string.startTime))
                    .execute();
            List<Event> items = events.getItems();


id=1;

            Log.d("RR","id: "+id+"");
            remindersList.clear();
            raw_remindersList.clear();

            int count=0;
            for (Event event : items) {
                Date start_date = new Date(event.getStart().getDateTime().getValue());
                String start_time = DateAndTimeUtil.toStringDateAndTime(start_date);
                Date end_date = new Date(event.getEnd().getDateTime().getValue());
                String end_time = DateAndTimeUtil.toStringDateAndTime(end_date);

                Calendar start_cal = Calendar.getInstance();
                start_cal.setTime(start_date);
                Calendar end_cal = Calendar.getInstance();
                end_cal.setTime(end_date);
                String raw_start_time = DateAndTimeUtil.toStringDateAndTime(start_cal);
                String raw_end_time = DateAndTimeUtil.toStringDateAndTime(end_cal);
                Log.d("MADBB", raw_start_time);

                String content = event.getDescription();
                String title = event.getSummary();
                String location = event.getLocation();

                Reminder raw_reminder = new Reminder(title, content, raw_end_time, raw_start_time, location, id);
                Reminder reminder = new Reminder(title, content, end_time, start_time, location);
                remindersList.add(reminder);

                raw_remindersList.add(raw_reminder);
                id=id+1;
                count=count+1;
            }

            SharedPreferences prefs = getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);

            String pre = prefs.getString(getString(R.string.spinner), getString(R.string.by_title));


            datasource.open();
            datasource.clearAll();
            for (Reminder r : remindersList) {
                datasource.create(r);
            }

            mReminderList.clear();
            if (pre.equals(getString(R.string.sort_event))) {

                mReminderList.addAll(datasource.findAll());
            } else {
                mReminderList.addAll(datasource.filterAll(pre));
            }

            datasource.close();


            for (Reminder reminder : raw_remindersList) {
                Log.d("RR",reminder.getId()+"");
                Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
                alarmIntent.putExtra(getString(R.string.notification_id), reminder.getId());
                Calendar calendar = DateAndTimeUtil.parseDateAndTime(reminder.getStartDateAndTime());
                calendar.set(Calendar.SECOND, 0);
                AlarmUtil.setAlarm(getApplicationContext(), alarmIntent, reminder.getId(), calendar);
            }
            return remindersList;
        }


        @Override
        protected void onPreExecute() {
            swipeContainer.setRefreshing(true);
            SharedPreferences sharedpreferences = getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            String text = spinner.getSelectedItem().toString();
            editor.putString(getString(R.string.spinner), text);
            editor.commit();

        }


        @Override
        protected void onPostExecute(ArrayList<Reminder> reminders) {
            super.onPostExecute(reminders);
            mAdapter.notifyDataSetChanged();

            swipeContainer.setRefreshing(false);
            fabb=true;
        FloatingActionButton fa= (FloatingActionButton) findViewById(fab);
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fa.getLayoutParams();

            fa.setLayoutParams(p);
            fa.setVisibility(View.VISIBLE);


        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            ViewActivity.REQUEST_AUTHORIZATION);
                } else {
                    Log.d(MAD, getString(R.string.failed) + mLastError.getMessage());
                }
            }
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button
        int id = item.getItemId();

        if (id == R.id.action_get_events) {
            getResultsFromApi();
        }
        if (id == R.id.action_delete) {
            datasource.open();
            datasource.clearAll();
            mAddReminder.clear();
            mReminderList.clear();
            mAdapter.notifyDataSetChanged();
            datasource.close();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(MAD, getString(R.string.on_forresult));
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }

        switch (resultCode) {
            case RESULT_OK:
                String title_info = data.getExtras().getString(getString(R.string.title_info));
                String start_time_info = data.getExtras().getString(getString(R.string.start_time_info));
                String end_time_info = data.getExtras().getString(getString(R.string.end_time_info));
                String content_info = data.getExtras().getString(getString(R.string.content_info));
                String location_info = data.getExtras().getString(getString(R.string.location_info));
                Reminder a_reminder = new Reminder(title_info, content_info, end_time_info, start_time_info, location_info);
                mAddReminder.add(a_reminder);
                datasource.open();
                datasource.create(a_reminder);
                mReminderList.clear();
                mReminderList.addAll(datasource.findAll());
                mAdapter.notifyDataSetChanged();
                Toast.makeText(getBaseContext(), R.string.insert_success, Toast.LENGTH_SHORT).show();
                mRecyclerView.setAdapter(mAdapter);
                datasource.close();//*
                break;
            default:
                break;
        }
    }
}
