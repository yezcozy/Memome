package com.mad.memome.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mad.memome.R;
import com.mad.memome.database.Datasource;
import com.mad.memome.receivers.AlarmReceiver;
import com.mad.memome.utils.AlarmUtil;
import com.mad.memome.utils.DateAndTimeUtil;

import java.util.Calendar;

import static com.mad.memome.utils.DateAndTimeUtil.toReadableStringDateAndTime;

/**
 * This class is used to add new customised activity or reminder
 * Created by Yijun Gai
 */
public class AddReminder extends AppCompatActivity {
    private Calendar m_end_calendar;
    private Calendar m_start_calendar;
    private Button save_btn;
    private Button cancel_btn;
    private LinearLayout start_ll;
    private LinearLayout end_ll;
    private EditText title_et;
    private EditText content_et;
    private TextView start_date_txt;
    private TextView end_date_txt;
    private TextView start_time_txt;
    private TextView end_time_txt;
    private TextView location_et;
    private Datasource datasource;
    private int id;
    private TextInputLayout tilContent, tilLocation;
    private static final int MAX_COUNT = 10;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_reminder);
        title_et = (EditText) findViewById(R.id.titleEt);
        content_et = (EditText) findViewById(R.id.contentEt);
        start_ll = (LinearLayout) findViewById(R.id.start_time);
        end_ll = (LinearLayout) findViewById(R.id.end_time);
        start_date_txt = (TextView) findViewById(R.id.start_date_txt);
        end_date_txt = (TextView) findViewById(R.id.end_date_txt);
        start_time_txt = (TextView) findViewById(R.id.start_time_txt);
        location_et = (TextView) findViewById(R.id.locationEt);
        end_time_txt = (TextView) findViewById(R.id.end_time_txt);
        m_start_calendar = Calendar.getInstance();
        m_end_calendar = Calendar.getInstance();
        datasource = new Datasource(this);
        datasource.open();
       // id = getIntent().getIntExtra(getString(R.string.notification_id), 0);
        tilContent = (TextInputLayout) findViewById(R.id.til_content);
        tilLocation= (TextInputLayout) findViewById(R.id.til_location);


        content_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (content_et.getText().toString().trim().length() == 0) {
                        content_et.setError("Content canot be empty");
                        tilContent.setError("Content canot be empty");
                    } else {

                            tilContent.setError(null);
                    }
                }

            }

        });

      //  location_et.set

        tilLocation.setCounterMaxLength(MAX_COUNT);
        location_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (location_et.getText().length() > MAX_COUNT) {
                    tilLocation.setCounterEnabled(true);
                    tilLocation.setErrorEnabled(true);
                    tilLocation.setError("Location's length must less than 10");
                } else if(location_et.getText().toString().trim().length() == 0){
                    tilLocation.setErrorEnabled(true);
                    location_et.setError("Location canot be empty");
                    tilLocation.setError("Location canot be empty");
                }
                else
                {
                    tilLocation.setCounterEnabled(false);
                    tilLocation.setError(null);
                  tilLocation.setErrorEnabled(false);

                }
            }
        });

   //     if (id == 0) {
            id = datasource.getLastNotificationId() + 1;
            datasource.close();

     //   }

        end_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                end_ll.getLayoutParams().width = ActionBar.LayoutParams.WRAP_CONTENT;

                DatePickerDialog DatePicker = new DatePickerDialog(AddReminder.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker DatePicker, int year, int month, int dayOfMonth) {
                        m_end_calendar.set(Calendar.YEAR, year);
                        m_end_calendar.set(Calendar.MONTH, month);
                        m_end_calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        end_date_txt.setText(DateAndTimeUtil.toStringReadableDate(m_end_calendar));
                    }
                }, m_end_calendar.get(Calendar.YEAR), m_end_calendar.get(Calendar.MONTH), m_end_calendar.get(Calendar.DAY_OF_MONTH));
                DatePicker.show();
                TimePickerDialog TimePicker = new TimePickerDialog(AddReminder.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(android.widget.TimePicker timePicker, int hour, int minute) {
                        m_end_calendar.set(Calendar.HOUR_OF_DAY, hour);
                        m_end_calendar.set(Calendar.MINUTE, minute);
                        end_time_txt.setText(DateAndTimeUtil.toStringReadableTime(m_end_calendar, getApplicationContext()));
                    }
                }, m_end_calendar.get(Calendar.HOUR_OF_DAY), m_end_calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getApplicationContext()));
                TimePicker.show();

            }
        });

        start_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start_ll.getLayoutParams().width = ActionBar.LayoutParams.WRAP_CONTENT;
                DatePickerDialog DatePicker = new DatePickerDialog(AddReminder.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker DatePicker, int year, int month, int dayOfMonth) {
                        m_start_calendar.set(Calendar.YEAR, year);
                        m_start_calendar.set(Calendar.MONTH, month);
                        m_start_calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        start_date_txt.setText(DateAndTimeUtil.toStringReadableDate(m_start_calendar));
                    }
                }, m_start_calendar.get(Calendar.YEAR), m_start_calendar.get(Calendar.MONTH), m_start_calendar.get(Calendar.DAY_OF_MONTH));
                DatePicker.show();
                TimePickerDialog TimePicker = new TimePickerDialog(AddReminder.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(android.widget.TimePicker timePicker, int hour, int minute) {
                        m_start_calendar.set(Calendar.HOUR_OF_DAY, hour);
                        m_start_calendar.set(Calendar.MINUTE, minute);
                        start_time_txt.setText(DateAndTimeUtil.toStringReadableTime(m_start_calendar, getApplicationContext()));
                    }
                }, m_start_calendar.get(Calendar.HOUR_OF_DAY), m_start_calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getApplicationContext()));
                TimePicker.show();
            }
        });

        save_btn = (Button) findViewById(R.id.save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar nowCalendar = Calendar.getInstance();


                // Check if selected date is before today's date
                if (DateAndTimeUtil.toLongDateAndTime(m_start_calendar) < DateAndTimeUtil.toLongDateAndTime(nowCalendar)) {
                    Toast.makeText(AddReminder.this, R.string.date_invalid,
                            Toast.LENGTH_LONG).show();


                    // Check if title is empty
                } else if (title_et.getText().toString().trim().isEmpty()) {
                    Toast.makeText(AddReminder.this, R.string.input_empty,
                            Toast.LENGTH_LONG).show();

                    // Check if times to show notification is too low
                } else if (DateAndTimeUtil.toLongDateAndTime(m_start_calendar) > DateAndTimeUtil.toLongDateAndTime(m_end_calendar)) {
                    Toast.makeText(AddReminder.this, R.string.end_before_start,
                            Toast.LENGTH_LONG).show();
                } else {
                    String title_info = title_et.getText().toString();

                    String start_time_info = toReadableStringDateAndTime(m_start_calendar);
                    String end_time_info = toReadableStringDateAndTime(m_end_calendar);


                    String content_info = content_et.getText().toString();
                    String location_info = location_et.getText().toString();

                    Intent intent = new Intent();
                    Log.d("EE",id+"");
                    intent.putExtra(getString(R.string.title_info), title_info);
                    intent.putExtra(getString(R.string.start_time_info), start_time_info);
                    intent.putExtra(getString(R.string.end_time_info), end_time_info);
                    intent.putExtra(getString(R.string.content_info), content_info);
                    intent.putExtra(getString(R.string.location_info), location_info);
                    Intent alarmIntent = new Intent(AddReminder.this, AlarmReceiver.class);
                    m_start_calendar.set(Calendar.SECOND, 0);
                    AlarmUtil.setAlarm(AddReminder.this, alarmIntent, id, m_start_calendar);

                    AddReminder.this.setResult(RESULT_OK, intent);

                    AddReminder.this.finish();

                }


            }
        });
        cancel_btn = (Button) findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
