package ru.spbau.ourpedometer;

import java.util.ArrayList;
import java.util.Date;

/**
 * User: Dmitriy Bandurin
 * Date: 20.04.12
 */
public class StatisticsCollector implements StatsReader, StatsSaver {
    ArrayList<StatisticsBean> values = new ArrayList<StatisticsBean>();
    private static final StatisticsCollector instance = new StatisticsCollector();

    public static StatisticsCollector getInstance(){
        return instance;
    }

    @Override
    public Iterable<StatisticsBean> getStatsByDateRange(Date startTime, Date stopTime) {
        return values;
    }

    @Override
    public void save(StatisticsBean statistics) {
        values.add(statistics);
    }
}
