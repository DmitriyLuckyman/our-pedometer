package ru.spbau.ourpedometer.persistens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public abstract class FlushingCollector implements StatisticsCollector {
    final protected ArrayList<StatisticsBean> buffer;
    final private int bufferCapacity;

    protected FlushingCollector(int bufferCapacity) {
        this.bufferCapacity = bufferCapacity;
        buffer = new ArrayList<StatisticsBean>(bufferCapacity);
    }

    @Override
    public void save(StatisticsBean statistics) {
        buffer.add(statistics);
        if (buffer.size() >= bufferCapacity)
        {
            flush(Collections.unmodifiableList(buffer));
            buffer.clear();
        }
    }

    @Override
    public Iterable<StatisticsBean> getStatsByDateRange(Date startTime, Date stopTime) {
        flush(buffer);
        return getStatsByDateRangeFromStorage(startTime, stopTime);
    }

    @Override
    public void close() {
        flush(buffer);
        closeStorage();
    }

    protected abstract Iterable<StatisticsBean> getStatsByDateRangeFromStorage(Date startTime, Date stopTime);
    protected abstract void flush(List<StatisticsBean> buffer);
    protected abstract void closeStorage();
}
