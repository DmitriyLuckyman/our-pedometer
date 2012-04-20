package ru.spbau.ourpedometer;

import java.util.Date;


public interface StatsReader {
    public Iterable<StatisticsBean> getStatsByDateRange(Date startTime, Date stopTime);
}
