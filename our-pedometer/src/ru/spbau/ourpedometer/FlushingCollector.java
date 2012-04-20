package ru.spbau.ourpedometer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class FlushingCollector extends StatisticsCollector {
    final protected ArrayList<StatisticsBean> buffer;
    final private int bufferCapacity;

    protected FlushingCollector(int bufferCapacity) {
        this.bufferCapacity = bufferCapacity;
        buffer = new ArrayList<StatisticsBean>(bufferCapacity);
    }

    protected List<StatisticsBean> getBuffer() {
        return Collections.unmodifiableList(buffer);
    }

    @Override
    public void save(StatisticsBean statistics) {
        buffer.add(statistics);
        if (buffer.size() >= bufferCapacity)
        {
            flush();
            buffer.clear();
        }
    }

    protected abstract void flush();
}
