package ru.spbau.ourpedometer.settingsactivity;

import android.widget.SeekBar;

public class SmartFloatSeekBarListener implements SmartValueListener<Float>, SeekBar.OnSeekBarChangeListener{
    private SeekBar bar;
    private SmartValue<Float> smartValue;
    private float min, max;

    public SmartFloatSeekBarListener(SeekBar bar, SmartValue<Float> smartValue, float min, float max) {
        this.min = min;
        this.max = max;
        this.bar = bar;
        this.smartValue = smartValue;
        smartValue.addListener(this);
    }
    
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        float value = (float)i / (float)bar.getMax();
        smartValue.setValue(max * value + min * (1.0f - value));
    }

    @Override
    public void onValueChanged(Float value) {
        bar.setProgress((int) ((value - min) / (max - min) * (float)bar.getMax()));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
