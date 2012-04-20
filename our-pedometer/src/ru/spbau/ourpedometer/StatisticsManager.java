package ru.spbau.ourpedometer;

public class StatisticsManager {
    private static final StatisticsCollector instance = new SQLiteStatsCollector("accel.db", 1000);

    public static StatisticsCollector getInstance() {
        return instance;
    }
}
