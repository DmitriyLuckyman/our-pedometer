package ru.spbau.ourpedometer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Calendar;

public class MainWidget extends AppWidgetProvider{
    private static Context context_t;
    private static RemoteViews views;
    private static AppWidgetManager wm;
    private static int[] ids;

    public static String ACTION_WIDGET_CONFIGURE = "ConfigureWidget";
    public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";
    private static int aimSteps = 0;
    private static int maxProgress = 100;
    private RemoteViews views2;
    private boolean binded = false;

    PedometerRemoteInterface aService;

    public static void setAimSteps(int aimSteps) {
        MainWidget.aimSteps = aimSteps;
        views.setProgressBar(R.id.process, maxProgress, ++aimSteps, false);
        wm.updateAppWidget(ids, views);
    }
    public void onUpdate(final Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        context.startService(new Intent(context, AccelerometerService.class));
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
                int steps = 0;
                if(!binded){
                    RemoteServiceConnection mConnection = new RemoteServiceConnection();
                   /* binded = context.bindService(new Intent(PedometerRemoteInterface.class.getName()),
                            mConnection, Context.BIND_AUTO_CREATE); */
                } else {
                    try {
                        steps = aService.getSteps();

                        views.setTextViewText(R.id.dayCounter, "Number: " + steps + "");
                        Log.v(this.getClass().getName(), "Steps=" + steps);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                views.setProgressBar(R.id.process, aimSteps, steps, false);
                //views.setTextViewText(R.id.hmCounter, "Speed: " + hoursLeft--);
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
              /*  Notification noty = new Notification(R.drawable.icon, "Button 1 clicked",
                        System.currentTimeMillis());
                noty.setLatestEventInfo(context, "Notice", msg, contentIntent);
                notificationManager.notify(1, noty);*/
            }
            super.onReceive(context, intent);
        }
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