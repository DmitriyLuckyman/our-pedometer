package ru.spbau.ourpedometer;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StepsCountActivity extends Activity {
    Button startButton;

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
