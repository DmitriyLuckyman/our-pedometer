package ru.spbau.ourpedometer.service;

import android.app.Service;
import android.content.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.util.Log;
import android.widget.EditText;
import ru.spbau.ourpedometer.PedometerRemoteInterface;
import ru.spbau.ourpedometer.persistens.StatisticsBean;
import ru.spbau.ourpedometer.persistens.StatisticsCalculator;
import ru.spbau.ourpedometer.persistens.StatisticsManager;
import ru.spbau.ourpedometer.settingsactivity.StepsCountActivity;

import java.sql.Time;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class AccelerometerService extends Service implements SensorEventListener, TextToSpeech.OnInitListener {

    public static final int FIRST_RUN = 2000;
    public static final int INTERVAL = 5000;
    public static final String STEPS_KEY = "steps";
    public static final String SPEED_KEY = "speed";
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Timer timer;

    private static Time timeSinceStart;

        private int MY_DATA_CHECK_CODE = 0;
        private TextToSpeech tts = null;
        HashMap<String, String> hashAlarm = null;

        private float maxSpeed = 4;
        private float minSpeed = 1;
        private int stepsCount = 900;
        private long totalTime = 5 * 60 * 1000;
        private long deltaSayTime = 20 * 1000;

        private boolean init = false;
        public static final String STEPS_BROADCAST_ACTION = "ru.spbau.ourpedometer.ACCELEROMETER_BROADCAST";
        private boolean isBroadCastRegister = false;

        private int delta = 0;
        private int remSteps = stepsCount;
        private long lastSayTime = 0;
        private long startTime;
        private double speed = 0;

        private boolean isShouldSay = false;

        private float getValue(EditText editText) {
            Editable text = null;
            if (editText != null) {
                text = editText.getText();
            }
            if (text != null) {
                return Float.valueOf(text.toString());
            }
            return -1;
        }

        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {

                int result = tts.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

                    Intent installIntent = new Intent();
                    installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startService(installIntent);
                } else {
                }

            } else if (status == TextToSpeech.ERROR) {
            }
        }


        private void start() {
            isShouldSay = true;

            if (tts == null) {
            } else {

                tts.speak("Let`s go!", TextToSpeech.QUEUE_FLUSH, hashAlarm);

                lastSayTime = Calendar.getInstance().getTimeInMillis();
                startTime = lastSayTime;

                if (!isBroadCastRegister) {

                    registerReceiver(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            if (Calendar.getInstance().getTimeInMillis() - lastSayTime >= deltaSayTime && isShouldSay) {
                                delta = intent.getIntExtra("steps", -1);
                                double newSpeed = intent.getIntExtra("speed", -1);
                                if (Math.abs(newSpeed - speed) > 1) {
                                    tts.speak("Your speed has become greater on "+(newSpeed - speed),
                                            TextToSpeech.QUEUE_FLUSH, hashAlarm);
                                    speed = newSpeed;
                                }
                                remSteps = stepsCount - delta;
                                if (remSteps < 0) {
                                    remSteps = 0;
                                    isShouldSay = false;
                                }
                                tts.speak("Remaining " + remSteps + " steps!", TextToSpeech.QUEUE_FLUSH, hashAlarm);
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                lastSayTime += deltaSayTime;

                            }
                            if (Calendar.getInstance().getTimeInMillis() - startTime >= totalTime) {
                                isShouldSay = false;
                            }
                        }
                    }, new IntentFilter(STEPS_BROADCAST_ACTION));
                }
            }
        }


    private BroadcastReceiver configChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            configureTimer(intent.getIntExtra(StepsCountActivity.RATE_STRING, INTERVAL));
            final StatisticsCalculator calculator = StatisticsManager.getCalculator();
            calculator.setStepWidthThreshold(intent.getIntExtra(StepsCountActivity.SENSITIVITY_STRING,
                    StepsCountActivity.DEFAULT_SENSITIVITY_VALUE));
        }
    };

    private IntentFilter configChangeIntentFilter = new IntentFilter(StepsCountActivity.OURPEDOMETER_CONFIG_CHANGED);

    @Override
    public void onCreate() {
        super.onCreate();
        startService();
        startDate();
        Log.v(this.getClass().getName(), "onCreate(..)");
        tts = new TextToSpeech(this, this);

        hashAlarm = new HashMap();
        hashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                String.valueOf(AudioManager.STREAM_MUSIC));

        start();
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
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }


    }

    private void configureTimer(int interval) {

        if(timer != null){
            timer.cancel();
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    Intent intent = new Intent(STEPS_BROADCAST_ACTION);
                    final StatisticsCalculator calculator = StatisticsManager.getCalculator();
                    final Date startTime = startDate();
                    final Date stopTime = new Date();
                    intent.putExtra(STEPS_KEY,
                            calculator.steps(startTime, stopTime));
                    intent.putExtra(SPEED_KEY, calculator.speed(startTime, stopTime, TimeUnit.SECONDS));
                    sendBroadcast(intent);
                } catch (Exception ex) {
                    Log.d(AccelerometerService.class.getName(), ex.getMessage());
                }
            }
        }, FIRST_RUN, interval);

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
            final int interval = sharedPreferences.getInt(StepsCountActivity.SENSITIVITY_STRING, INTERVAL);
            configureTimer(interval);
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
            return StatisticsManager.getCalculator().steps(startDate(), new Date());
        }

        @Override
        public float getSpeed() throws RemoteException {
            return StatisticsManager.getCalculator().speed(startDate(), new Date(), TimeUnit.SECONDS);
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
        if (timeSinceStart == null) {
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