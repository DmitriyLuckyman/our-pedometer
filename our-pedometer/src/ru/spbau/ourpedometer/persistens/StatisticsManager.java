package ru.spbau.ourpedometer.persistens;

public class StatisticsManager {
    private static final LightCollectorCalculator instance = new LightCollectorCalculator(new MemoryStepsSaver());

    public static StatisticsCalculator getCalculator(){
        return instance;
    }

    public static StatsSaver getSaver() {
        return instance;
    }

}
