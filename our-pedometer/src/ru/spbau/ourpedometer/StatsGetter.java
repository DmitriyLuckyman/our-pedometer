package ru.spbau.ourpedometer;

import java.util.Date;


public interface StatsGetter {
    public void initGetter (Date start);
    public Statistics getNext();
}
