package ru.spbau.ourpedometer;

import java.util.ArrayList;
import java.util.Date;

public class MemoryStatisticsCollector implements StatisticsCollector {
    final private ArrayList<StatisticsBean> data;

    public MemoryStatisticsCollector () {
        data = new ArrayList<StatisticsBean>();
    }
    
    @Override
    public Iterable<StatisticsBean> getStatsByDateRange(Date startTime, Date stopTime) {
        return data;
    }

    @Override
    public void save(StatisticsBean statistics) {
        data.add(statistics);
    }
}
