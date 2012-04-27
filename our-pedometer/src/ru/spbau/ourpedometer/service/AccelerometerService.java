package ru.spbau.ourpedometer.service;

import android.app.Service;
import android.content.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import ru.spbau.ourpedometer.PedometerRemoteInterface;
import ru.spbau.ourpedometer.persistens.StatisticsBean;
import ru.spbau.ourpedometer.persistens.StatisticsCalculator;
import ru.spbau.ourpedometer.persistens.StatisticsManager;
import ru.spbau.ourpedometer.settingsactivity.StepsCountActivity;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class AccelerometerService extends Service implements SensorEventListener{
    public static final String LOG_TAG = "PEDOMETER";

    public static final String STEPS_BROADCAST_ACTION = "ru.spbau.ourpedometer.ACCELEROMETER_BROADCAST";

    public static final String STEPS_KEY = "steps";
    public static final String SPEED_KEY = "speed";
    public static final String MIN_SPEED_KEY = "minSpeed";
    public static final String MAX_SPEED_KEY = "maxSpeed";

    public static final boolean DEFAULT_AUTORUN_VALUE = false;
    public static final int DEFAULT_RATE_VALUE = 1;
    public static final int DEFAULT_SENSITIVITY_VALUE = 40;
    public static final int DEFAULT_V_SENSITIVITY_VALUE = 2;
    public static final int DEFAULT_INTERVAL_VALUE = 5000;
    public static final int FIRST_RUN = 2000;

    private SensorManager sensorManager;

    private Sensor accelerometerSensor;

    private Timer timer;

    private final TimerTask sendBroadcastTask = new TimerTask() {
        @Override
        public void run() {
            try {
                Intent intent = new Intent(STEPS_BROADCAST_ACTION);
                final StatisticsCalculator calculator = StatisticsManager.getCalculator();
                final Date startTime = startDate();
                final Date stopTime = new Date();
                intent.putExtra(STEPS_KEY,     calculator.steps(startTime, stopTime));
                intent.putExtra(SPEED_KEY,     calculator.speed(startTime, stopTime, TimeUnit.SECONDS));
                intent.putExtra(MIN_SPEED_KEY, calculator.minSpeed(startTime, stopTime, TimeUnit.SECONDS));
                intent.putExtra(MAX_SPEED_KEY, calculator.maxSpeed(startTime, stopTime, TimeUnit.SECONDS));
                sendBroadcast(intent);
            } catch (Exception ex) {
                Log.e(LOG_TAG, "Error while send a broadcast" + ex.getMessage(), ex);
            }
        }
    };

    private static long startTimeInMillis;

    final private BroadcastReceiver configChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            configureTimer(intent.getIntExtra(StepsCountActivity.INTERVAL_KEY, DEFAULT_INTERVAL_VALUE));
            final StatisticsCalculator calculator = StatisticsManager.getCalculator();
            calculator.setStepWidthThreshold(intent.getIntExtra(StepsCountActivity.SENSITIVITY_KEY,
                    DEFAULT_SENSITIVITY_VALUE));
            calculator.setStepHeightThreshold(intent.getIntExtra(StepsCountActivity.V_SENSITIVITY_KEY,
                    DEFAULT_V_SENSITIVITY_VALUE));
            startTimeInMillis = intent.getLongExtra(StepsCountActivity.TIME_KEY, System.currentTimeMillis());
        }
    };

    final private IntentFilter configChangeIntentFilter = new IntentFilter(StepsCountActivity.OURPEDOMETER_CONFIG_CHANGED);

    @Override
    public void onCreate() {
        super.onCreate();
        startService();
        startDate();
        Log.v(LOG_TAG, "Pedometer Service Started");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG, "Bind Performed");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        sensorManager.unregisterListener(this, accelerometerSensor);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (Sensor.TYPE_ACCELEROMETER == sensorEvent.sensor.getType()) {
            StatisticsBean statisticsBean = new StatisticsBean(sensorEvent.values, System.currentTimeMillis());
            StatisticsManager.getSaver().save(statisticsBean);
            Log.v(LOG_TAG, "saveStatistics");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private void configureTimer(int interval) {

        if(timer != null){
            timer.cancel();
        }

        timer = new Timer();
        timer.schedule(sendBroadcastTask, FIRST_RUN, interval);
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
            registerReceiver(configChangeReceiver, configChangeIntentFilter);
            final SharedPreferences sharedPreferences = getSharedPreferences(StepsCountActivity.PREFS_NAME, MODE_PRIVATE);
            final int interval = sharedPreferences.getInt(StepsCountActivity.INTERVAL_KEY, DEFAULT_INTERVAL_VALUE);
            configureTimer(interval);
        }
    }

    private final PedometerRemoteInterface.Stub mBinder = new PedometerRemoteInterface.Stub() {

        @Override
        public int getSteps() throws RemoteException {
            return StatisticsManager.getCalculator().steps(startDate(), new Date());
        }

        @Override
        public float getSpeed() throws RemoteException {
            return StatisticsManager.getCalculator().speed(startDate(), new Date(), TimeUnit.SECONDS);
        }

        @Override
        public float getMaxSpeed() throws RemoteException {
            return StatisticsManager.getCalculator().maxSpeed(startDate(), new Date(), TimeUnit.SECONDS);
        }

        @Override
        public float getMinSpeed() throws RemoteException {
            return StatisticsManager.getCalculator().minSpeed(startDate(), new Date(), TimeUnit.SECONDS);
        }

    };

   private Date startDate() {
        return new Date(startTimeInMillis);
    }
}