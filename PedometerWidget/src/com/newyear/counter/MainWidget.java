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

public class MainWidget extends AppWidgetProvider{
    private static Context context_t;
    private static RemoteViews views;
    private static AppWidgetManager wm;
    private static int[] ids;

    public static String ACTION_WIDGET_CONFIGURE = "ConfigureWidget";
    public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";
    private static int progress = 100;
    private static int maxProgress = 100;
    private RemoteViews views2;
    private int steps = 0;

    public static final String STEPS_BROADCAST_ACTION = "ru.spbau.ourpedometer.ACCELEROMETER_BROADCAST";

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
        views.setOnClickPendingIntent(R.id.progress, configPendingIntent);

        Intent active = new Intent(context, MainWidget.class);
        active.setAction(ACTION_WIDGET_RECEIVER);
        active.putExtra("msg", "Pedometer has started");
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
        try {
            actionPendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }

        wm.updateAppWidget(appWidgetIds, views);
        wm.updateAppWidget(appWidgetIds, views2);

        CountDownTimer timer = new CountDownTimer (1000000,3000) {
            int daysLeft = 0;
            int hoursLeft = 5;

            public void onTick(long millisUntilFinished) { //выполняем регулярное действие
                //меняем текст виджета
                views.setProgressBar(R.id.progress, progress, steps, false);
                views.setTextViewText(R.id.dayCounter, "Steps: " + steps + "");
                wm.updateAppWidget(ids, views);
            }

            public void onFinish() {
            }

        }.start();
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
                Notification noty = new Notification(R.drawable.icon, "Pedometer has started!",
                        System.currentTimeMillis());
                noty.flags = Notification.FLAG_ONGOING_EVENT;
                noty.setLatestEventInfo(context, "Notice", msg, contentIntent);
                notificationManager.notify(1, noty);
            }
            super.onReceive(context, intent);
        }
    }
}