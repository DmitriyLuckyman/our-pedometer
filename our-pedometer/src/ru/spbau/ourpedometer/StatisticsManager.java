package ru.spbau.ourpedometer;

public class StatisticsManager {
    private static final LightCollectorCalculator instance = new LightCollectorCalculator(new MemoryStepsSaver());

    public static StatsReader getReader() {
        return instance;
    }

    public static StatisticsCalculator getCalculator(){
        return instance;
    }

    public static StatsSaver getSaver() {
        return instance;
    }

}
