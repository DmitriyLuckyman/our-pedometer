package ru.spbau.ourpedometer.settingsactivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import ru.spbau.ourpedometer.R;

public class StepsCountActivity extends Activity {
    private static float MIN_SENSITIVITY = 0.0f, MAX_SENSITIVITY = 1.0f;
    private static float MIN_RATE = 0.5f, MAX_RATE = 10.0f;

    private static final String PREFS_NAME = "OurPedometerPrefs";
    private static final String SENSITIVITY_STRING = "sensitivity";
    private static final String RATE_STRING = "rate";

    private SharedPreferences settings;

    
    private SeekBar sensitivityBar;
    private TextView sensitivityValueLabel;
    private SeekBar rateBar;
    private TextView rateValueLabel;

    private Button saveButton;
    private Button cancelButton;

    private SmartValue<Float> sensitivity;
    private SmartValue<Float> rate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = getSharedPreferences(PREFS_NAME, 0);
        sensitivity = new SmartValue<Float>(settings.getFloat(SENSITIVITY_STRING, (MIN_SENSITIVITY + MAX_SENSITIVITY) * 0.5f));
        rate = new SmartValue<Float>(settings.getFloat(RATE_STRING, (MIN_RATE + MAX_RATE) * 0.5f));

        setContentView(R.layout.main);

        sensitivityBar = (SeekBar)findViewById(R.id.sensitivity_bar);
        sensitivityBar.setOnSeekBarChangeListener(new SmartFloatSeekBarListener(sensitivityBar, sensitivity, 0.0f, 1.0f));
        sensitivityValueLabel = (TextView)findViewById(R.id.sensitivity_value_label);
        sensitivity.addListener(new SmartValueListener<Float>() {
            @Override
            public void onValueChanged(Float value) {
                sensitivityValueLabel.setText("" + sensitivity.getValue());
            }
        });

        rateBar = (SeekBar)findViewById(R.id.rate_bar);
        rateBar.setOnSeekBarChangeListener(new SmartFloatSeekBarListener(rateBar, rate, 0.5f, 15.0f));
        rateValueLabel = (TextView)findViewById(R.id.rate_value_label);
        rate.addListener(new SmartValueListener<Float>() {
            @Override
            public void onValueChanged(Float value) {
                rateValueLabel.setText("" + rate.getValue());
            }
        });

        saveButton = (Button)findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putFloat(SENSITIVITY_STRING, sensitivity.getValue());
                editor.putFloat(RATE_STRING, rate.getValue());
                editor.commit();

                finish();
            }
        });
        cancelButton = (Button)findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }



    /*private static int floatToBar (float val, float min, float max, int barMax) {
        return (int)((val - min) / (max - min) * (float)barMax);
    }
    private static float barToFloat (int val, float min, float max, int barMax) {
        float t = (float)val / (float)barMax;
        return max * t + min * (1 - t);
    }*/

    /*Button startButton;

    TextView valueX;
    TextView valueY;


    TextView valueZ;
    TextView number;
    TextView speed;
    PedometerRemoteInterface aService;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int steps = intent.getIntExtra("steps", -1);
            number.setText("" + steps);
            Log.v(this.getClass().getName(), "Steps=" + steps);
        }
    };


    private boolean buttonClicked;
    private IntentFilter intentFilter = new IntentFilter(AccelerometerService.STEPS_BROADCAST_ACTION);
    View.OnClickListener startListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            registerReceiver(broadcastReceiver, intentFilter);
            buttonClicked = true;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        if(buttonClicked){
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(buttonClicked){
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        valueX = (TextView) findViewById(R.id.value_x);
        valueY = (TextView) findViewById(R.id.value_y);
        valueZ = (TextView) findViewById(R.id.value_z);
        number = (TextView) findViewById(R.id.num);
        speed = (TextView) findViewById(R.id.speed);

        startButton = (Button) findViewById(R.id.button_start);
        startButton.setOnClickListener(startListener);
    }

    class RemoteServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName className, IBinder service) {
            aService = PedometerRemoteInterface.Stub.asInterface(service);
        }

        public void onServiceDisconnected(ComponentName className) {
            aService = null;
        }
    }*/

}
