package ru.spbau.ourpedometer;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Date: 19.04.12
 * Time: 15:16
 */
public interface StatisticsCollector {
    public void start();
    public void stop();
    public void save(Statistics statistics);
    public List<Statistics> get();
}
