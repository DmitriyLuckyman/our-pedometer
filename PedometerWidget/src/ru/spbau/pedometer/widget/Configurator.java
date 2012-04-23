package ru.spbau.pedometer.widget;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class Configurator extends Activity {

    private static final String COLOR_PREFERENCE_KEY = "color";

    private static int maxSteps = 200;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure);
        setMaxSteps();

        final Button textColorBtn = (Button) findViewById(R.id.textColor);
        textColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = PreferenceManager.getDefaultSharedPreferences(
                        Configurator.this).getInt(COLOR_PREFERENCE_KEY,
                        Color.WHITE);
                new ColorPickerDialog(Configurator.this, new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void colorChanged(int color) {
                        PreferenceManager.getDefaultSharedPreferences(Configurator.this).edit().putInt(
                                COLOR_PREFERENCE_KEY, color).commit();
                        MainWidget.setTextColor(color);
                    }
                }, color).show();
                Toast.makeText(getApplicationContext(), "Color of text was set.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        final Button circleColorBtn = (Button) findViewById(R.id.circleColor);
        circleColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int color = PreferenceManager.getDefaultSharedPreferences(
                        Configurator.this).getInt(COLOR_PREFERENCE_KEY,
                        Color.WHITE);
                new ColorPickerDialog(Configurator.this, new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void colorChanged(int color) {
                        PreferenceManager.getDefaultSharedPreferences(Configurator.this).edit().putInt(
                                COLOR_PREFERENCE_KEY, color).commit();
                        MainWidget.setCircleColor(color);
                    }
                }, color).show();
                Toast.makeText(getApplicationContext(), "Color of progress was set.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                maxSteps = progress;
                TextView max = (TextView)findViewById(R.id.maxLabel);
                max.setText(String.valueOf(progress));
                MainWidget.setMaxSteps(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        Toast.makeText(getApplicationContext(), "You may change settings",
                Toast.LENGTH_SHORT).show();
    }

    private void setMaxSteps() {
        seekBar = ((SeekBar) findViewById(R.id.maxStepsBar));
        seekBar.setProgress(maxSteps);
        TextView max = (TextView)findViewById(R.id.maxLabel);
        max.setText(String.valueOf(maxSteps));
    }


}
