package ru.spbau.ourpedometer.persistens;

import java.util.Date;


public interface StatsReader {
    public Iterable<StatisticsBean> getStatsByDateRange(Date startTime, Date stopTime);
}
