package ru.spbau.ourpedometer;
/**
 * User: Dmitriy Bandurin
 * Date: 20.04.12
 */
interface PedometerRemoteInterface {

    int getSteps();
    float getSpeed();
    float getMaxSpeed();
    float getMinSpeed();
}
