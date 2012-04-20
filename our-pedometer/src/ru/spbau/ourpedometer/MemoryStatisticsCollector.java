package ru.spbau.ourpedometer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MemoryStatisticsCollector implements StatisticsCollector {
    final private ArrayList<StatisticsBean> data;

    public MemoryStatisticsCollector () {
        data = new ArrayList<StatisticsBean>();
    }

    @Override
    public Iterable<StatisticsBean> getStatsByDateRange(Date startTime, Date stopTime) {
        List<StatisticsBean> result = new ArrayList<StatisticsBean>();
        synchronized (data){
            Collections.copy(result, data);
        }
        return result;
    }

    @Override
    public void save(StatisticsBean statistics) {
        data.add(statistics);
    }
}
