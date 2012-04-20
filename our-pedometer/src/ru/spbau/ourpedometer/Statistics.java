package ru.spbau.ourpedometer;

public class Statistics {
    private Float[] coordinates;
    private Long time;  // msec

    public Statistics(Float x, Float y, Float z, Long time){
        coordinates = new Float[]{x, y, z};
        this.time = time;
    }

    public Float x(){
        return coordinates[0];
    }

    public Float y(){
        return coordinates[1];
    }

    public Float z(){
        return coordinates[2];
    }

    public Long time(){
        return time;
    }
}
