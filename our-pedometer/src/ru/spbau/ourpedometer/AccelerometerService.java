package ru.spbau.ourpedometer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.sql.Time;
import java.util.*;

public class AccelerometerService extends Service implements SensorEventListener {

    public static final int FIRST_RUN = 2000;
    public static final int INTERVAL = 5000;
    public static final String STEPS_BROADCAST_ACTION = "ru.spbau.ourpedometer.ACCELEROMETER_BROADCAST";
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Timer timer;

    private static Time timeSinceStart;

    @Override
    public void onCreate() {
        super.onCreate();
        startService();
        Log.v(this.getClass().getName(), "onCreate(..)");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(this.getClass().getName(), "onBind(..)");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        sensorManager.unregisterListener(this, accelerometerSensor);
    }

    private void startService() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (!sensors.isEmpty()) {
            for (Sensor sensor : sensors) {
                switch (sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        if (accelerometerSensor == null) accelerometerSensor = sensor;
                        break;
                }
                if (accelerometerSensor != null) {
                    sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
                    break;
                }
            }
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {

                        Intent intent = new Intent(STEPS_BROADCAST_ACTION);
                        intent.putExtra("steps",
                                StatisticsManager.getCalculator().steps(startDate(), new Date()));
                        sendBroadcast(intent);
                    } catch (Exception ex) {
                        Log.d(AccelerometerService.class.getName(), ex.getMessage());
                    }
                }
            }, FIRST_RUN, INTERVAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (Sensor.TYPE_ACCELEROMETER == sensorEvent.sensor.getType()) {
            StatisticsBean statisticsBean = new StatisticsBean(sensorEvent.values[0],
                    sensorEvent.values[1], sensorEvent.values[2], System.currentTimeMillis());
            StatisticsManager.getSaver().save(statisticsBean);
            Log.v(this.getClass().getName(), "saveStatistics");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private final PedometerRemoteInterface.Stub mBinder = new PedometerRemoteInterface.Stub() {

        @Override
        public int getSteps() throws RemoteException {
            return new StatsCalculator(StatisticsManager.getReader()).steps(new Date(0), new Date());
        }
    };

    public static void setTimeSinceStart(Time timeSinceStart) {
        AccelerometerService.timeSinceStart = timeSinceStart;
    }

    public static Time getTimeSinceStart() {
        return timeSinceStart;
    }

    public static Date startDate() {
        Calendar cal = Calendar.getInstance();
        if(timeSinceStart == null) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            return cal.getTime();
        }
        Calendar change = Calendar.getInstance();
        change.setTime(timeSinceStart);
        cal.set(Calendar.HOUR_OF_DAY, change.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, change.get(Calendar.MINUTE));
        cal.set(Calendar.SECOND, change.get(Calendar.SECOND));
        return cal.getTime();
    }
}