package ru.spbau.ourpedometer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import ru.spbau.ourpedometer.service.AccelerometerService;
import ru.spbau.ourpedometer.settingsactivity.StepsCountActivity;

public class OnBootReceiver extends BroadcastReceiver {

    public static final boolean DEFAULT_AUTORUN_VALUE = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            SharedPreferences settings = context.getSharedPreferences(StepsCountActivity.PREFS_NAME, Context.MODE_PRIVATE);
            final boolean runService = settings.getBoolean(StepsCountActivity.AUTORUN_KEY, DEFAULT_AUTORUN_VALUE);
            if(runService){
                Intent serviceLauncher = new Intent(context, AccelerometerService.class);
                context.startService(serviceLauncher);
                Log.v(this.getClass().getName(), "Service loaded while device boot.");
            }
        }
    }
}