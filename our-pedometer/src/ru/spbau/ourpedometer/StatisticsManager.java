package ru.spbau.ourpedometer;

public class StatisticsManager {
    private static final StatisticsCollector instance = new LightCollectorCalculator(new MemoryStepsSaver());

    public static StatisticsCollector getInstance() {
        return instance;
    }
}
