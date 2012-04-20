package ru.spbau.ourpedometer;

public class StatisticsManager {
    private static final StatisticsCollector instance = new MemoryStatisticsCollector();

    public static StatisticsCollector getInstance() {
        return instance;
    }
}
