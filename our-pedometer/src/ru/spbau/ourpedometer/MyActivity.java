package ru.spbau.ourpedometer;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity implements SensorEventListener
{
    Button startButton;

    SensorManager sensorManager;
    Sensor accelerometerSensor;

    TextView valueX;
    TextView valueY;
    TextView valueZ;

    List<List<Float>> values;
    boolean isRecording = false;

    BufferedWriter writer;


    View.OnClickListener startListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            isRecording = !isRecording;
            if(isRecording) {
                values.clear();

                try{

                    File file = new File(Environment.getExternalStorageDirectory() + File.separator + "test.txt");
                    writer = new BufferedWriter(new FileWriter(file));

                    writer.newLine();
                    writer.write("Start!");


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        values = new ArrayList<List<Float>>();
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(!sensors.isEmpty()) {
            for (Sensor sensor : sensors) {
                switch(sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        if(accelerometerSensor == null) accelerometerSensor = sensor;
                        break;
                }
                if(accelerometerSensor != null){
                    break;
                }
            }
        }



        valueX = (TextView)findViewById(R.id.value_x);
        valueY = (TextView)findViewById(R.id.value_y);
        valueZ = (TextView)findViewById(R.id.value_z);

        startButton = (Button)findViewById(R.id.button_start);
        startButton.setOnClickListener(startListener);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(!isRecording) return;
        final List<Float> lValues = new ArrayList<Float>();
        try {
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error during collecting statistics!");
            e.printStackTrace();
        }
        for(float value: sensorEvent.values) {
            lValues.add(value);
            try {
                writer.write(String.valueOf(value) + " ");
            } catch (IOException e) {
                System.err.println("Error during collecting statistics!");
                e.printStackTrace();
            }
        }

        try {
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error during saving statistics!");
            e.printStackTrace();
        }

        switch(sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                valueX.setText(String.valueOf(sensorEvent.values[SensorManager.DATA_X]));
                valueY.setText(String.valueOf(sensorEvent.values[SensorManager.DATA_Y]));
                valueZ.setText(String.valueOf(sensorEvent.values[SensorManager.DATA_Z]));
                values.add(lValues);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
