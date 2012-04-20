package ru.spbau.ourpedometer;

import android.util.Log;

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
            result.addAll(data);
        }
        Log.d(MemoryStatisticsCollector.class.getName(),"" + result.size());
        return result;
    }

    @Override
    public void save(StatisticsBean statistics) {
        data.add(statistics);
    }

    @Override
    public void close() {
        data.clear();
    }
}
