package ru.spbau.ourpedometer;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface StatisticsCalculator {
    public void setStepHeightThreshold(float threshold);
    public void setStepWidthThreshold(int threshold);

    public int steps(Date startTime, Date stopTime);

    public float speed(Date startTime, Date stopTime, TimeUnit timeUnit);
    public float minSpeed(Date startTime, Date stopTime, TimeUnit timeUnit);
    public float maxSpeed(Date startTime, Date stopTime, TimeUnit timeUnit);
}
