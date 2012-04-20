package ru.spbau.ourpedometer;

import java.util.Date;


public interface StatsGetter {
    public void init (Date start);
    public Statistics getNext();
}
