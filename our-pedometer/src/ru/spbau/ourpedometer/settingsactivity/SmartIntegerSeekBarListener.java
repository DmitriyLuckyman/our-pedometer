package ru.spbau.ourpedometer.settingsactivity;

import android.widget.SeekBar;

public class SmartIntegerSeekBarListener implements SmartValueListener<Integer>, SeekBar.OnSeekBarChangeListener{
    private SeekBar bar;
    private SmartValue<Integer> smartValue;

    public SmartIntegerSeekBarListener(SeekBar bar, SmartValue<Integer> smartValue) {
        this.bar = bar;
        this.smartValue = smartValue;
        smartValue.addListener(this);
    }

    @Override
    public void onValueChanged(Integer value) {}

    @Override
    public void onProgressChanged(SeekBar seekBar, int value, boolean isUserInteractive) {
        bar.setProgress(value);
        smartValue.setValue(value);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
