package ru.spbau.ourpedometer.settingsactivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import ru.spbau.ourpedometer.R;
import ru.spbau.ourpedometer.service.AccelerometerService;

import java.util.Calendar;

public class StepsCountActivity extends Activity {

    public static final String PREFS_NAME = "OurPedometerPrefs";

    public static final String SENSITIVITY_KEY = "sensitivity";
    public static final String V_SENSITIVITY_KEY = "v_sensitivity";
    public static final String INTERVAL_KEY = "rate";
    public static final String AUTORUN_KEY = "autorun";
    public static final String TIME_KEY = "timeKey";

    public static final String OURPEDOMETER_CONFIG_CHANGED = "ru.spbau.ourpedometer.CONFIG_CHANGED";

    private final View.OnClickListener configSaveHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SharedPreferences.Editor editor = settings.edit();
            final Integer sens = sensitivity.getValue();
            final Integer v_sens = v_sensitivity.getValue();
            final Integer rateValue = rate.getValue();
            final long timeInMillis = time.getTimeInMillis();

            editor.putInt(SENSITIVITY_KEY, sens);
            editor.putInt(V_SENSITIVITY_KEY, v_sens);
            editor.putInt(INTERVAL_KEY, rateValue);
            editor.putBoolean(AUTORUN_KEY, autorun);
            editor.putLong(TIME_KEY, timeInMillis);
            editor.commit();

            Intent configChangeIntent = new Intent(OURPEDOMETER_CONFIG_CHANGED);
            configChangeIntent.putExtra(SENSITIVITY_KEY, sens);
            configChangeIntent.putExtra(V_SENSITIVITY_KEY, v_sens);
            configChangeIntent.putExtra(INTERVAL_KEY, rateValue);
            configChangeIntent.putExtra(TIME_KEY, timeInMillis);
            sendBroadcast(configChangeIntent);
        }
    };

    private SharedPreferences settings;

    private SmartValue<Integer> sensitivity;
    private SmartValue<Integer> v_sensitivity;
    private SmartValue<Integer> rate;
    private boolean autorun;
    private TextView mTimeDisplay;

    private Calendar time;

    static final int TIME_DIALOG_ID = 0;
    private final View.OnClickListener timePickHandler = new View.OnClickListener() {
        public void onClick(View v) {
            showDialog(TIME_DIALOG_ID);
        }
    };

    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    time.set(Calendar.MINUTE, minute);
                    updateTime();
                }
            };

    private final View.OnClickListener closeHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    private final CompoundButton.OnCheckedChangeListener autorunChangeHandler = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            autorun = b;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sensitivity = new SmartValue<Integer>(settings.getInt(SENSITIVITY_KEY, AccelerometerService.DEFAULT_SENSITIVITY_VALUE));
        v_sensitivity = new SmartValue<Integer>(settings.getInt(V_SENSITIVITY_KEY, AccelerometerService.DEFAULT_V_SENSITIVITY_VALUE));
        rate = new SmartValue<Integer>(settings.getInt(INTERVAL_KEY, AccelerometerService.DEFAULT_RATE_VALUE));
        autorun = settings.getBoolean(AUTORUN_KEY, AccelerometerService.DEFAULT_AUTORUN_VALUE);
        time = Calendar.getInstance();
        time.setTimeInMillis(settings.getLong(TIME_KEY, 0));

        configureSeekBar(R.id.sensitivity_bar, R.id.sensitivity_value_label, sensitivity);
        configureSeekBar(R.id.v_sensitivity_bar, R.id.v_sensitivity_value_label, v_sensitivity);
        configureSeekBar(R.id.rate_bar, R.id.rate_value_label, rate);

        CheckBox autorunCheckBox = (CheckBox)findViewById(R.id.autorun_check_box);
        autorunCheckBox.setChecked(autorun);
        autorunCheckBox.setOnCheckedChangeListener(autorunChangeHandler);

        findViewById(R.id.save_button).setOnClickListener(configSaveHandler);
        findViewById(R.id.close_button).setOnClickListener(closeHandler);
        findViewById(R.id.pickTime).setOnClickListener(timePickHandler);

        mTimeDisplay = (TextView) findViewById(R.id.timeDisplay);

        updateTime();
    }

    private void configureSeekBar(final int barID, final int labelID, final SmartValue<Integer> value) {
        SeekBar bar = (SeekBar) findViewById(barID);
        final TextView label = (TextView)findViewById(labelID);
        bar.setOnSeekBarChangeListener(new SmartIntegerSeekBarListener(bar, value));
        value.addListener(new SmartValueListener<Integer>() {
            @Override
            public void onValueChanged(Integer value) {
                label.setText("" + value);
            }
        });
    }

    private void updateTime() {
        final int mHour = time.get(Calendar.HOUR_OF_DAY);
        final int mMinute = time.get(Calendar.MINUTE);
        StringBuilder sb = new StringBuilder()
                .append(pad(mHour)).append(":")
                .append(pad(mMinute));
        mTimeDisplay.setText(sb);
        sb.append(":00");
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final int mHour = time.get(Calendar.HOUR_OF_DAY);
        final int mMinute = time.get(Calendar.MINUTE);
        switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        mTimeSetListener, mHour, mMinute, true);
        }
        return null;
    }
}
