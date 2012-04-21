package ru.spbau.ourpedometer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TempFileStatsSaver implements StatsSaver {
    BufferedWriter writer;

    TempFileStatsSaver(String path) {
        try {
            writer = new BufferedWriter(new FileWriter(path));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public void save(StatisticsBean s) {
        try {
            writer.write("" + s.time() + "; " + s.x() + "; " + s.y() + "; " + s.z());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
