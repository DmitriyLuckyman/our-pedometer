package ru.spbau.ourpedometer;

/**
 * Created with IntelliJ IDEA.
 * Date: 19.04.12
 * Time: 15:08
 */
public class Statistics {
    Float[] coordinates;
    Long time;

    public Statistics(Float x, Float y, Float z, Long time){
        coordinates = new Float[]{x, y, z};
        this.time = time;
    }
}
