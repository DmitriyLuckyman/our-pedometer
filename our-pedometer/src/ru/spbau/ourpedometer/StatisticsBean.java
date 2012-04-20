package ru.spbau.ourpedometer;

public class StatisticsBean {
    private Float[] coordinates;
    private Long time;  // msec

    public StatisticsBean(Float x, Float y, Float z, Long time){
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
