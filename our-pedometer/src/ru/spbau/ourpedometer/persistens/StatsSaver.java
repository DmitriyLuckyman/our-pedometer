package ru.spbau.ourpedometer.persistens;

public interface StatsSaver {
    public void save(StatisticsBean statistics);
    public void close();
}

