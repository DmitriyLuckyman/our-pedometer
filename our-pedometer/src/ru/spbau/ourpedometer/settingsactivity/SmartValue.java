package ru.spbau.ourpedometer.settingsactivity;

import java.util.LinkedList;
import java.util.List;

public class SmartValue<T> {
    private T value;
    private final List<SmartValueListener<T>> listeners;
    
    public SmartValue (T value) {
        this.value = value;
        this.listeners = new LinkedList<SmartValueListener<T>>();
    }
    
    public T getValue() {
        return value;
    }
    
    public void setValue(T value) {
        this.value = value;
        for (SmartValueListener<T> listener : listeners)
            listener.onValueChanged(value);
    }

    public void addListener(SmartValueListener<T> listener) {
        if (listener == null)
            throw new NullPointerException();

        listeners.add(listener);
        listener.onValueChanged(value);
    }
}
