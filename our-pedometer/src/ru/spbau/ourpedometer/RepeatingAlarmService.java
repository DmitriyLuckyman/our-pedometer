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
        final IBinder iBinder = peekService(context, new Intent(context, AccelerometerService.class));
        PedometerRemoteInterface aService = PedometerRemoteInterface.Stub.asInterface(iBinder);
        final int steps;
        try {
            steps = aService.getSteps();
            intent.putExtra("steps", steps);
            context.sendBroadcast(intent);
            Toast.makeText(context, "Now : " + steps, Toast.LENGTH_LONG).show();
            Log.v(this.getClass().getName(), "Timed alarm onReceive() started at time: " + new java.sql.Timestamp(System.currentTimeMillis()).toString());
        } catch (RemoteException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}