package ru.spbau.ourpedometer.settingsactivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import ru.spbau.ourpedometer.service.AccelerometerService;
import ru.spbau.ourpedometer.R;

import java.sql.Time;
import java.util.Calendar;

public class StepsCountActivity extends Activity {

    public static final String PREFS_NAME = "OurPedometerPrefs";
    public static final String SENSITIVITY_STRING = "sensitivity";
    public static final String RATE_STRING = "rate";
    public static final int DEFAULT_RATE_VALUE = 1;
    public static final int DEFAULT_SENSITIVITY_VALUE = 40;
    public static final String OURPEDOMETER_CONFIG_CHANGED = "ru.spbau.ourpedometer.CONFIG_CHANGED";

    private SharedPreferences settings;

    private TextView sensitivityValueLabel;
    private TextView rateValueLabel;

    private SmartValue<Integer> sensitivity;
    private SmartValue<Integer> rate;
    private TextView mTimeDisplay;
    private Button mPickTime;
    private Button saveButton;
    
    private int mHour;
    private int mMinute;

    static final int TIME_DIALOG_ID = 0;

    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mHour = hourOfDay;
                    mMinute = minute;
                    updateTime();
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sensitivity = new SmartValue<Integer>(settings.getInt(SENSITIVITY_STRING, DEFAULT_SENSITIVITY_VALUE));
        rate = new SmartValue<Integer>(settings.getInt(RATE_STRING, DEFAULT_RATE_VALUE));

        setContentView(R.layout.main);

        SeekBar sensitivityBar = (SeekBar) findViewById(R.id.sensitivity_bar);
        sensitivityBar.setOnSeekBarChangeListener(new SmartIntegerSeekBarListener(sensitivityBar, sensitivity));
        sensitivityValueLabel = (TextView)findViewById(R.id.sensitivity_value_label);
        sensitivity.addListener(new SmartValueListener<Integer>() {
            @Override
            public void onValueChanged(Integer value) {
                sensitivityValueLabel.setText("" + sensitivity.getValue());
            }
        });

        SeekBar rateBar = (SeekBar) findViewById(R.id.rate_bar);
        rateBar.setOnSeekBarChangeListener(new SmartIntegerSeekBarListener(rateBar, rate));
        rateValueLabel = (TextView)findViewById(R.id.rate_value_label);
        rate.addListener(new SmartValueListener<Integer>() {
            @Override
            public void onValueChanged(Integer value) {
                rateValueLabel.setText("" + rate.getValue());
            }
        });

        saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = settings.edit();
                final Integer sens = sensitivity.getValue();
                editor.putInt(SENSITIVITY_STRING, sens);
                final Integer rateValue = rate.getValue();
                editor.putInt(RATE_STRING, rateValue);
                editor.commit();
                Intent configChangeIntent = new Intent(OURPEDOMETER_CONFIG_CHANGED);
                configChangeIntent.putExtra(SENSITIVITY_STRING, sens);
                configChangeIntent.putExtra(RATE_STRING, rateValue);
                sendBroadcast(configChangeIntent);
            }
        });

        Button cancelButton = (Button) findViewById(R.id.close_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mTimeDisplay = (TextView) findViewById(R.id.timeDisplay);

        mPickTime = (Button) findViewById(R.id.pickTime);
        mPickTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });

        final Calendar c = Calendar.getInstance();
        if (AccelerometerService.getTimeSinceStart() != null)
            c.setTime(AccelerometerService.getTimeSinceStart());
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        updateTime();
    }

    private void updateTime() {
        StringBuilder sb = new StringBuilder()
                .append(pad(mHour)).append(":")
                .append(pad(mMinute));
        mTimeDisplay.setText(sb);
        sb.append(":00");
        AccelerometerService.setTimeSinceStart(Time.valueOf(sb.toString()));
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        mTimeSetListener, mHour, mMinute, true);
        }
        return null;
    }
}
