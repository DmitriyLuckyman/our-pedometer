package ru.spbau.ourpedometer;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class StatsCalculator implements StatisticsCalculator {
    private StatsReader reader;

    private float stepHeightThreshold = 12;
    private int stepWidthThreshold = 300;


    public StatsCalculator(StatsReader reader) {
        this.reader = reader;
    }

    @Override
    public int steps(Date startTime, Date stopTime) {
        Iterable<StatisticsBean> statsList = reader.getStatsByDateRange(startTime, stopTime);
        int steps = 0;

        long beginStepTime = startTime.getTime();//statsList.get(0).time();
        boolean stepFlag = false;
        boolean beginStepTimeInitialized = false; 

        for(StatisticsBean st : statsList){
            if (!beginStepTimeInitialized)
            {
                beginStepTime = st.time();
                beginStepTimeInitialized = true;
            }
            
            
            if (Math.sqrt(Math.pow(st.x(), 2) + Math.pow(st.z(), 2) + Math.pow(st.z(), 2)) > stepHeightThreshold) {
                if (!stepFlag) {
                    beginStepTime = st.time();
                    stepFlag = true;
                }
            } else {
                if (stepFlag) {
                    if (st.time() - beginStepTime > stepWidthThreshold) {
                        steps++;
                    }
                    stepFlag = false;
                }
            }
        }

        return steps/2;
    }

    @Override
    public void setStepHeightThreshold(float threshold) {
        stepHeightThreshold = threshold;
    }

    @Override
    public void setStepWidthThreshold(int threshold) {
        stepWidthThreshold = threshold;
    }

    private int currentTimeInMillis(TimeUnit timeUnit){
        switch (timeUnit){
            case MINUTES:
                return  60*1000;

            case SECONDS:
                return  1000;

            default:
                return  1000;
        }
    }

    @Override
    public float speed(Date startTime, Date stopTime, TimeUnit timeUnit) {
        int steps = steps(startTime, stopTime);
        return steps/ currentTimeInMillis(timeUnit);
    }

    public float minSpeed(Date startTime, Date stopTime, TimeUnit timeUnit) {
        int delta = currentTimeInMillis(timeUnit);
        Long time = startTime.getTime() + delta;
        float min = speed(startTime, new Date(time), timeUnit);


        while(time + delta <= stopTime.getTime()){
            float speed = speed(new Date(time), new Date(time + delta), timeUnit);
            if(speed < min){
                min = speed;
            }

            time += delta;
        }

        return min;
    }

    public float maxSpeed(Date startTime, Date stopTime, TimeUnit timeUnit) {
        int delta = currentTimeInMillis(timeUnit);
        Long time = startTime.getTime() + delta;
        float max = speed(startTime, new Date(time), timeUnit);


        while(time + delta <= stopTime.getTime()){
            float speed = speed(new Date(time), new Date(time + delta), timeUnit);
            if(speed > max){
                max = speed;
            }

            time += delta;
        }

        return max;
    }
}
