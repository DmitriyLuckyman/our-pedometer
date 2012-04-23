package ru.spbau.ourpedometer.persistens;

import ru.spbau.ourpedometer.persistens.StatisticsBean;

public interface StatsSaver {
    public void save(StatisticsBean statistics);
    public void close();
}

