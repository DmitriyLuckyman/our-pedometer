package ru.spbau.ourpedometer;

import android.os.Environment;

import java.io.File;

public class StatisticsManager {
    private static final LightCollectorCalculator instance = new LightCollectorCalculator(new MemoryStepsSaver());
    private static final StatsSaver saver = new TempFileStatsSaver (Environment.getExternalStorageDirectory()
                                                                    + File.separator + "temp_stats.txt");

    public static StatsReader getReader() {
        return instance;
    }

    public static StatisticsCalculator getCalculator(){
        return instance;
    }

    public static StatsSaver getSaver() {
        return saver;
    }

}
