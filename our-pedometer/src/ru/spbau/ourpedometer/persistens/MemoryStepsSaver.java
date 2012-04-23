package ru.spbau.ourpedometer.persistens;

import java.util.ArrayList;
import java.util.Date;

public class MemoryStepsSaver implements StepsSaver {
    final private ArrayList<Long> steps = new ArrayList<Long>(1000);

    @Override
    public void addStep(Date date) {
        steps.add(date.getTime());
    }

    @Override
    public int getStepsCount(Date start, Date end) {
        int sum = 0;
        final long startMs = start.getTime();
        final long endMs = end.getTime();
        for (Long step : steps)
            if (step >= startMs && step < endMs)
                ++sum;

        return sum;
    }

    @Override
    public void close() {

    }
}
