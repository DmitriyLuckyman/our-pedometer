package ru.spbau.ourpedometer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileStatCollector implements StatisticsCollector {
    private String fileName;
    private List<Statistics> statList = new ArrayList<Statistics>();
    private int capacity = 100;

    private Long startTime = new Long(-1);
    private Long curTime = new Long(0);

    // statistics:
    private int stepDoubleCount = 0;
    private int stepThreshop = 12;
    private boolean stepFlag = false;

    public FileStatCollector(String fileName) {
        this.fileName = fileName;
    }

    private void flush() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName)));

        for (Statistics st : statList) {
            writer.write(st.x() + " " + st.y() + " " + st.z() + st.time());
        }

        statList.clear();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void save(Statistics st) throws IOException {
        if(startTime == -1){
            startTime = st.time();
        }

        if (Math.sqrt(Math.pow(st.x(), 2) + Math.pow(st.z(), 2) + Math.pow(st.z(), 2)) > stepThreshop) {
            if(stepFlag == false){
                stepFlag = true;
            }
            stepDoubleCount++;
        }else{

        }

        statList.add(st);
        if (statList.size() > 100) {
            flush();
        }
    }

    @Override
    public List<Statistics> get() throws IOException {
        return get(curTime - startTime);
    }

    @Override
    public List<Statistics> get(Long time) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
            List<Statistics> data = new ArrayList<Statistics>();

            String s;
            try {
                while ((s = reader.readLine()) != null) {
                    String[] stat = s.split(" ");

                    if (curTime - Long.valueOf(stat[4]) < time) {
                        data.add(new Statistics(Float.valueOf(stat[0]), Float.valueOf(stat[1]),
                                Float.valueOf(stat[2]), Long.valueOf(stat[4])));
                    }else{
                        break;
                    }
                }

                for (Statistics st : statList) {
                    if (curTime - Long.valueOf(st.time()) < time) {
                        data.add(new Statistics(Float.valueOf(st.x()), Float.valueOf(st.y()),
                                Float.valueOf(st.z()), Long.valueOf(st.time())));
                    }else{
                        break;
                    }
                }

                return data;

            } catch (IOException e) {
                throw new IOException();
            }

        } catch (FileNotFoundException e) {
            throw new IOException();
        }
    }

    @Override
    public int steps() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int speed() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
