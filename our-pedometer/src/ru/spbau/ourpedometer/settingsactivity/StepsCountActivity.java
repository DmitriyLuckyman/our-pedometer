package ru.spbau.ourpedometer.settingsactivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import ru.spbau.ourpedometer.AccelerometerService;
import ru.spbau.ourpedometer.R;

import java.sql.Time;
import java.util.Calendar;

public class StepsCountActivity extends Activity {
    private static float MIN_SENSITIVITY = 0.0f, MAX_SENSITIVITY = 1.0f;
    private static float MIN_RATE = 0.5f, MAX_RATE = 10.0f;

    private static final String PREFS_NAME = "OurPedometerPrefs";
    private static final String SENSITIVITY_STRING = "sensitivity";
    private static final String RATE_STRING = "rate";

    private SharedPreferences settings;

    
    private SeekBar sensitivityBar;
    private TextView sensitivityValueLabel;
    private SeekBar rateBar;
    private TextView rateValueLabel;

    private Button saveButton;
    private Button cancelButton;

    private SmartValue<Float> sensitivity;
    private SmartValue<Float> rate;

    private TextView mTimeDisplay;
    private Button mPickTime;

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

        settings = getSharedPreferences(PREFS_NAME, 0);
        sensitivity = new SmartValue<Float>(settings.getFloat(SENSITIVITY_STRING, (MIN_SENSITIVITY + MAX_SENSITIVITY) * 0.5f));
        rate = new SmartValue<Float>(settings.getFloat(RATE_STRING, (MIN_RATE + MAX_RATE) * 0.5f));

        setContentView(R.layout.main);

        sensitivityBar = (SeekBar)findViewById(R.id.sensitivity_bar);
        sensitivityBar.setOnSeekBarChangeListener(new SmartFloatSeekBarListener(sensitivityBar, sensitivity, 0.0f, 1.0f));
        sensitivityValueLabel = (TextView)findViewById(R.id.sensitivity_value_label);
        sensitivity.addListener(new SmartValueListener<Float>() {
            @Override
            public void onValueChanged(Float value) {
                sensitivityValueLabel.setText("" + sensitivity.getValue());
            }
        });

        rateBar = (SeekBar)findViewById(R.id.rate_bar);
        rateBar.setOnSeekBarChangeListener(new SmartFloatSeekBarListener(rateBar, rate, 0.5f, 15.0f));
        rateValueLabel = (TextView)findViewById(R.id.rate_value_label);
        rate.addListener(new SmartValueListener<Float>() {
            @Override
            public void onValueChanged(Float value) {
                rateValueLabel.setText("" + rate.getValue());
            }
        });

        saveButton = (Button)findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putFloat(SENSITIVITY_STRING, sensitivity.getValue());
                editor.putFloat(RATE_STRING, rate.getValue());
                editor.commit();

                finish();
            }
        });
        cancelButton = (Button)findViewById(R.id.cancel_button);
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
                        mTimeSetListener, mHour, mMinute, false);
        }
        return null;
    }
}
