package ru.spbau.ourpedometer;

import android.app.Application;
import android.content.Intent;

/**
 * User: Dmitriy Bandurin
 * Date: 21.04.12
 */
public class PedometerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                startService(new Intent(getApplicationContext(), AccelerometerService.class));
            }
        });
        th.start();
    }
}
