package ru.spbau.ourpedometer.persistens;

public class StatisticsBean {
    private float[] coordinates;
    private long time;

    public StatisticsBean(Float x, Float y, Float z, Long time){
        coordinates = new float[]{x, y, z};
        this.time = time;
    }

    public StatisticsBean(float[] coordinates, long time) {
        this.coordinates = coordinates;
        this.time = time;
    }

    public float x(){
        return coordinates[0];
    }

    public float y(){
        return coordinates[1];
    }

    public float z(){
        return coordinates[2];
    }

    public long time(){
        return time;
    }
}
