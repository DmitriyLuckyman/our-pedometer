package ru.spbau.ourpedometer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MyActivity extends Activity {
    Button startButton;

    TextView valueX;
    TextView valueY;


    TextView valueZ;
    TextView number;
    TextView speed;
    PedometerRemoteInterface aService;

    private TimerTask timerTask;
    View.OnClickListener startListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if(!binded){
                RemoteServiceConnection mConnection = new RemoteServiceConnection();
                binded = bindService(new Intent(PedometerRemoteInterface.class.getName()),
                                    mConnection, Context.BIND_AUTO_CREATE);
            } else {
                try {
                    number.setText(aService.getSteps());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private boolean binded;


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Called when the activity is first created.
     */
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
    }

}
