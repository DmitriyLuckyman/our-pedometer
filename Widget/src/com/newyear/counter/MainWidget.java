package com.newyear.counter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Calendar;

public class MainWidget extends AppWidgetProvider{
    private static Context context_t;
    private static RemoteViews views;
    private static AppWidgetManager wm;
    public static final String STEPS_BROADCAST_ACTION = "ru.spbau.ourpedometer.ACCELEROMETER_BROADCAST";

    private static int[] ids;

    public static String ACTION_WIDGET_CONFIGURE = "ConfigureWidget";
    public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";
    private static int progress = 100;
    private static int maxProgress = 100;
    private RemoteViews views2;
    private int steps = 0;

    public static void setProgress(int progress) {
        MainWidget.progress = progress;
        views.setProgressBar(R.id.progress, maxProgress, ++progress, false);
        wm.updateAppWidget(ids, views);
    }
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                steps = intent.getIntExtra("steps", -1);
            }
        }, new IntentFilter(STEPS_BROADCAST_ACTION));
        context_t = context;
        views = new RemoteViews(context_t.getPackageName(), R.layout.step_widget);
        views2 = new RemoteViews(context_t.getPackageName(), R.layout.configure);
        wm = appWidgetManager;
        ids = appWidgetIds;

        Intent configIntent = new Intent(context, ClickOneActivity.class);
        configIntent.setAction(ACTION_WIDGET_CONFIGURE);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
        views.setOnClickPendingIntent(R.id.button_two, configPendingIntent);

        Intent active = new Intent(context, MainWidget.class);
        active.setAction(ACTION_WIDGET_RECEIVER);
        active.putExtra("msg", "Message for Button 1");
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
        views.setOnClickPendingIntent(R.id.button_one, actionPendingIntent);

        wm.updateAppWidget(appWidgetIds, views);
        wm.updateAppWidget(appWidgetIds, views2);

        CountDownTimer timer = new CountDownTimer (1000000,3000) {
            int daysLeft = 10;
            int hoursLeft = 5;

            public void onTick(long millisUntilFinished) { //выполняем регулярное действие
                //меняем текст виджета
                views.setProgressBar(R.id.progress, progress, steps, false);
                views.setTextViewText(R.id.dayCounter, "Number: " + steps);
                views.setTextViewText(R.id.hmCounter, "Speed: " + hoursLeft--);
                wm.updateAppWidget(ids, views);
            }

            public void onFinish() {
            }

        }.start();
    }

    public long dateToMilliSeconds(int day, int month, int year, int hour, int minute)  {

        Calendar c = Calendar.getInstance();
        c.set(year, month, day, hour, minute, 00);

        return c.getTimeInMillis();

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
            final int appWidgetId = intent.getExtras().getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                this.onDeleted(context, new int[] { appWidgetId });
            }
        } else {
            if (intent.getAction().equals(ACTION_WIDGET_RECEIVER)) {
                String msg = "null";
                try {
                    msg = intent.getStringExtra("msg");
                } catch (NullPointerException e) {
                    Log.e("Error", "msg = null");
                }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
                NotificationManager notificationManager =
                        (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification noty = new Notification(R.drawable.icon, "Button 1 clicked",
                        System.currentTimeMillis());
                noty.setLatestEventInfo(context, "Notice", msg, contentIntent);
                notificationManager.notify(1, noty);
            }
            super.onReceive(context, intent);
        }
    }
    /*
    private class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {

            String res = arg1.getExtras().getString("steps");
            //steps = Integer.parseInt(res);
            steps++;
        }

    } */

}