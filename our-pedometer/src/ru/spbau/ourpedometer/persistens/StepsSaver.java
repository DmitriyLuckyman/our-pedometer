package ru.spbau.ourpedometer.persistens;

import java.util.Date;

public interface StepsSaver {
    public void addStep(Date date);
    public int getStepsCount(Date start, Date end);
    public void close();
}
