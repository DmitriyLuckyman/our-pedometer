package ru.spbau.ourpedometer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import java.util.Date;
import java.util.List;

public class AccelerometerService extends Service implements SensorEventListener {

    public static final int FIRST_RUN = 2000;
    public static final int INTERVAL= 1000;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private AlarmManager alarmManager;
    private static final int REQUEST_CODE = 1234567890;

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
        sensorManager.unregisterListener(this , accelerometerSensor);
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
                    sensorManager.registerListener(this, accelerometerSensor,SensorManager.SENSOR_DELAY_GAME );
                    break;
                }
            }
            Intent intent = new Intent(this, RepeatingAlarmService.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, 0);

                    alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.setRepeating(
                            AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            SystemClock.elapsedRealtime() + FIRST_RUN,
                            INTERVAL,
                            pendingIntent);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(Sensor.TYPE_ACCELEROMETER == sensorEvent.sensor.getType()) {
            StatisticsBean statisticsBean = new StatisticsBean(sensorEvent.values[0],
                    sensorEvent.values[1], sensorEvent.values[2], System.currentTimeMillis());
            StatisticsManager.getInstance().save(statisticsBean);
            Log.v(this.getClass().getName(), "saveStatistics");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private final PedometerRemoteInterface.Stub mBinder = new PedometerRemoteInterface.Stub() {

        @Override
        public int getSteps() throws RemoteException {
            return new StatsCalculator(StatisticsManager.getInstance()).steps(new Date(0), new Date());
        }
    };
}