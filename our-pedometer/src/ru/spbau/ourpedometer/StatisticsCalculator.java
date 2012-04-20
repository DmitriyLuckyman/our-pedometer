package ru.spbau.ourpedometer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface StatisticsCalculator {
    public void start();
    public void stop();

    public void save(Statistics statistics) throws IOException;

    public List<Statistics> get() throws IOException;
    public List<Statistics> get(Long time) throws IOException;

    public int steps();
    public int steps(Long time);

    public int speed();
    public int speed(Long time);
}
