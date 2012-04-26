package com.example;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class DecisionManager extends Activity implements TextToSpeech.OnInitListener {
    private TextToSpeech tts = null;
    HashMap<String, String> hashAlarm = null;

    private float maxSpeed = 4;
    private float minSpeed = 1;
    private int totalStepsCount = 900;
    private long totalWalkTime = 5 * 60 * 1000;
    private long deltaSayTime = 20 * 1000;

    private Button startButton;
    private Button stopButton;


    public static final String STEPS_BROADCAST_ACTION = "ru.spbau.ourpedometer.ACCELEROMETER_BROADCAST";
    private boolean isBroadCastRegister = false;

    private long lastSayTime = 0;
    private long startSayTime;
    private double oldSpeed;

    private boolean isShouldSay = false;
    private boolean isBegin = true;

    private BroadcastReceiver stepsBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (Calendar.getInstance().getTimeInMillis() - lastSayTime >= deltaSayTime) {
                int stepsLeft = totalStepsCount - intent.getIntExtra("steps", -1);
                double newSpeed = intent.getIntExtra("speed", -1);

                if (Math.abs(newSpeed - oldSpeed) > 1) {
                    say("Your speed has become greater on " + (newSpeed - oldSpeed));
                }

                if (newSpeed > maxSpeed) {
                    say("You are too fast!");
                }

                if (newSpeed < minSpeed) {
                    say("You are going too slow!");
                }

                oldSpeed = newSpeed;

                if (stepsLeft < 0) {
                    say("All steps done!");
                    isShouldSay = false;
                } else {
                    say("Remaining " + stepsLeft + " steps!");
                }

                lastSayTime += deltaSayTime;

            }

            if (Calendar.getInstance().getTimeInMillis() - startSayTime >= totalWalkTime) {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                isBegin = true;
                isShouldSay = false;
            }
        }
    };

    private final View.OnClickListener startButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // max speed value
            float value = getValue((EditText) findViewById(R.id.maxSpeedValue));
            if (value != -1) {
                maxSpeed = value;
            }

            // min speed value
            value = getValue((EditText) findViewById(R.id.minSpeedValue));
            if (value != -1) {
                minSpeed = value;
            }

            // steps count value
            value = getValue((EditText) findViewById(R.id.stepsValue));
            if (value != -1) {
                totalStepsCount = (int) value;
            }

            // h : m : s
            int h = 0, m = 0, s = 0;

            value = getValue((EditText) findViewById(R.id.timeHourValue));
            if (value != -1) {
                h = (int) value;
            }

            value = getValue((EditText) findViewById(R.id.timeMinuteValue));
            if (value != -1) {
                m = (int) value;
            }

            value = getValue((EditText) findViewById(R.id.timeSecValue));
            if (value != -1) {
                s = (int) value;
            }

            if (h != 0 && m != 0 && s != 0) {
                totalWalkTime = 1000 * (h * 60 * 60 + m * 60 + s);
            }

            // delta say time value
            value = getValue((EditText) findViewById(R.id.deltaValue));
            if (value != -1) {
                deltaSayTime = 1000 * (int) value + 1000 * 15;
            }

            stopButton.setEnabled(true);
            isShouldSay = true;
            start();
        }
    };
    private final View.OnClickListener stopButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isShouldSay = false;
            startButton.setEnabled(true);
        }
    };
    private final View.OnClickListener exitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onDestroy();
        }
    };

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


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        startButton = (Button) findViewById(R.id.startButton);
        startButton.setEnabled(false);

        startButton.setOnClickListener(startButtonListener);

        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setEnabled(false);

        stopButton.setOnClickListener(stopButtonListener);

        Button exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setEnabled(true);

        exitButton.setOnClickListener(exitListener);

        tts = new TextToSpeech(this, this);

        hashAlarm = new HashMap<String, String>();
        hashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));

        start();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);

                Toast.makeText(DecisionManager.this,
                        "Language not supported", Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(DecisionManager.this,
                        "Language supported!", Toast.LENGTH_LONG).show();
            }

            Toast.makeText(DecisionManager.this,
                    "Text-To-Speech engine is initialized", Toast.LENGTH_LONG).show();

            startButton.setEnabled(true);

        } else if (status == TextToSpeech.ERROR) {
            Toast.makeText(DecisionManager.this,
                    "Error occurred while initializing Text-To-Speech engine", Toast.LENGTH_LONG).show();
        }
    }

    private void say(String text) {
        if (isShouldSay) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, hashAlarm);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void start() {

        if (tts == null) {
            Toast.makeText(DecisionManager.this,
                    "Error occurred while initializing Text-To-Speech engine", Toast.LENGTH_LONG).show();
        } else {
            if (isBegin) {
                say("Let`s go!");
                lastSayTime = System.currentTimeMillis();
                startSayTime = lastSayTime;
                oldSpeed = 0;
                isBegin = false;
            }


            if (!isBroadCastRegister) {
                isBroadCastRegister = true;
                registerReceiver(stepsBroadCastReceiver, new IntentFilter(STEPS_BROADCAST_ACTION));
            }
        }
    }


    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        super.onDestroy();
    }

}
