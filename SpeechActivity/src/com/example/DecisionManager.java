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
    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech tts = null;
    HashMap<String, String> hashAlarm = null;

    private float maxSpeed = 4;
    private float minSpeed = 1;
    private int stepsCount = 900;
    private long totalTime = 5 * 60 * 1000;
    private long deltaSayTime = 20 * 1000;

    private boolean init = false;

    private Button startButton;
    private Button stopButton;
    private Button exitButton;

    private Data data = new Data();

    //    public DecisionManager(Data data){
    //        this.data = data;
    //    }

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


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        startButton = (Button) findViewById(R.id.startButton);
        startButton.setEnabled(false);

        startButton.setOnClickListener(new View.OnClickListener() {
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
                    stepsCount = (int) value;
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
                    totalTime = 1000 * (h * 60 * 60 + m * 60 + s);
                }

                // delta say time value
                value = getValue((EditText) findViewById(R.id.deltaValue));
                if (value != -1) {
                    deltaSayTime = (int) value * 1000;
                }

                start();
            }
        });

        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setEnabled(false);

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShouldSay = false;
                startButton.setEnabled(true);
            }
        });

        exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setEnabled(true);

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroy();
            }
        });


        tts = new TextToSpeech(this, this);

        hashAlarm = new HashMap();
        hashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                String.valueOf(AudioManager.STREAM_MUSIC));

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


    private void start() {
        startButton.setEnabled(false);
        isShouldSay = true;

        if (tts == null) {
            Toast.makeText(DecisionManager.this,
                    "NULL!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(DecisionManager.this,
                    "Say!", Toast.LENGTH_LONG).show();

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
                            Toast.makeText(DecisionManager.this,
                                    "!!!!!! delta = " + delta, Toast.LENGTH_LONG).show();
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
                            startButton.setEnabled(true);
                            stopButton.setEnabled(false);
                        }
                    }
                }, new IntentFilter(STEPS_BROADCAST_ACTION));
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
