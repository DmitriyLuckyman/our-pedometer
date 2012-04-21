package ru.spbau.ourpedometer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class RepeatingAlarmService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final int steps = intent.getIntExtra("steps", 1000);
        Toast.makeText(context, "Now : " + steps, 100).show();
        Log.v(this.getClass().getName(), "Timed alarm onReceive() started at time: " + new java.sql.Timestamp(System.currentTimeMillis()).toString());
    }
}