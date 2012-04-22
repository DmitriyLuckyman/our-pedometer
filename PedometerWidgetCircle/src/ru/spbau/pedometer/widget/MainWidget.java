package ru.spbau.pedometer.widget;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.*;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MainWidget extends AppWidgetProvider {
    private static Context context_t;
    private static RemoteViews views;
    private static AppWidgetManager wm;
    private static int[] ids;

    private static int textColor = 0xFFFFFFFF;
    private static int circleColor = 0xFFFF0000;
    private static int secondCircleColor = 0x10FF0000;

    private static Paint mPaints;
    private static RectF mBigOval;
    private static float mSweep;

    public static String ACTION_WIDGET_CONFIGURE = "ConfigureWidget";
    public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";
    private static int maxSteps = 10000;
    private RemoteViews views2;
    private static int steps = 0;

    public static final String STEPS_BROADCAST_ACTION = "ru.spbau.ourpedometer.ACCELEROMETER_BROADCAST";


    public static void setMaxSteps(int max) {
        MainWidget.maxSteps = max;
        drawProgress();
    }

    public static void setTextColor(int color) {
        MainWidget.textColor = color;
        drawProgress();
    }

    public static void setCircleColor(int color) {
        MainWidget.circleColor = color;
        MainWidget.secondCircleColor = Color.argb(Color.alpha(0x10000000), Color.red(color), Color.green(color), Color.blue(color));
        drawProgress();
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        mPaints = new Paint();
        mPaints.setAntiAlias(true);
        mPaints.setStyle(Paint.Style.STROKE);

        mPaints.setStrokeWidth(10);
        mPaints.setColor(circleColor);

        mBigOval = new RectF(10, 10, 90, 90);

        context.getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                steps = intent.getIntExtra("steps", -1);
                drawProgress();
            }
        }, new IntentFilter(STEPS_BROADCAST_ACTION));
        context_t = context;
        views = new RemoteViews(context_t.getPackageName(), R.layout.step_widget);


        views2 = new RemoteViews(context_t.getPackageName(), R.layout.configure);
        wm = appWidgetManager;
        ids = appWidgetIds;

        Intent configIntent = new Intent(context, Configurator.class);
        configIntent.setAction(ACTION_WIDGET_CONFIGURE);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
        views.setOnClickPendingIntent(R.id.imageView, configPendingIntent);

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
    }

    private static void drawProgress() {
        final Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        canvas.setBitmap(bitmap);
        canvas.drawColor(Color.argb(0, 0, 0, 0));

        drawArcs(canvas, mBigOval, false, mPaints);

        mSweep = (float) (steps * 1.0 / maxSteps * 360);
        if (mSweep > 360) {
            mSweep -= 360;
        }

        views.setImageViewBitmap(R.id.imageView, bitmap);

        wm.updateAppWidget(ids, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
            final int appWidgetId = intent.getExtras().getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                this.onDeleted(context, new int[]{appWidgetId});
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
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                /*                Notification.Builder builder = new Notification.Builder(context);
                builder.setOngoing(true);
                builder.setContentTitle("Pedometer");
                builder.setContentText("Pedometer has started!");
                builder.setSmallIcon(R.drawable.icon);
                builder.setSmallIcon(R.drawable.icon);
                builder.setWhen(System.currentTimeMillis());
                builder.setContentIntent(contentIntent);
                notificationManager.notify(1, builder.getNotification());*/
                Notification noty = new Notification(R.drawable.icon, "Pedometer has started!",
                        System.currentTimeMillis());
                noty.flags = Notification.FLAG_ONGOING_EVENT;
                noty.setLatestEventInfo(context, "Notice", msg, contentIntent);
                notificationManager.notify(1, noty);
            }
            super.onReceive(context, intent);
        }
    }

    private static void drawArcs(Canvas canvas, RectF oval, boolean useCenter,
                                 Paint paint) {
        Paint secondOval = new Paint();
        secondOval.setAntiAlias(true);
        secondOval.setStyle(Paint.Style.STROKE);
        secondOval.setStrokeWidth(6);
        secondOval.setColor(secondCircleColor);

        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        textPaint.setTextSize(30);
        String progress = String.valueOf((int)Math.abs(steps * 100.0 / maxSteps)) + "%";
        if(maxSteps < steps) progress = "100%";
        canvas.drawText(progress, oval.centerX() - 10 * progress.length(), oval.centerY() + 10, textPaint);

        canvas.drawArc(oval, 0, 360, false, secondOval);
        canvas.drawArc(oval, 0, mSweep, useCenter, paint);
    }

}