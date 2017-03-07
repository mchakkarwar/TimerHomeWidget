package com.example.poc_home_widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mahesh Chakkarwar on 27-10-2016.
 */

public class HomeWidgetProvider extends AppWidgetProvider implements TimerCallbacks {
    private long timeElapsedInMillis = 0;
    private String APP_WIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE";
    private String START_TIMER_ACTION = "com.example.poc_home_widget.widget.START";
    private String PAUSE_TIMER_ACTION = "com.example.poc_home_widget.widget.PAUSE";
    private String STOP_TIMER_ACTION = "com.example.poc_home_widget.widget.STOP";
    private String RESUME_TIMER_ACTION = "com.example.poc_home_widget.widget.RESUME";
    private AppWidgetManager appWidgetManager;
    private int[] appWidgetIds;
    private boolean mIsStart = false;
    private boolean mIsPause = false;
    private boolean mIsStop = false;
    private boolean mIsResume = false;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        this.appWidgetManager = appWidgetManager;
        this.appWidgetIds = appWidgetIds;

        for (int i = 0; i < appWidgetIds.length; i++) {
            int widgetId = appWidgetIds[i];
            updateWidget(context, appWidgetManager, widgetId);
        }
    }

    public void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);

        remoteViews.setTextViewText(R.id.textViewTitle, "00:00:00");
        remoteViews.setViewVisibility(R.id.buttonStart, View.VISIBLE);
        remoteViews.setOnClickPendingIntent(R.id.buttonStart, getPendingSelfIntent(context, START_TIMER_ACTION));

        if (mIsStart) {
            remoteViews.setViewVisibility(R.id.buttonStart, View.GONE);
            remoteViews.setViewVisibility(R.id.buttonPause, View.VISIBLE);
            remoteViews.setOnClickPendingIntent(R.id.buttonPause, getPendingSelfIntent(context, PAUSE_TIMER_ACTION));
            remoteViews.setViewVisibility(R.id.buttonStop, View.VISIBLE);
            remoteViews.setOnClickPendingIntent(R.id.buttonStop, getPendingSelfIntent(context, STOP_TIMER_ACTION));
        }

        if (mIsPause) {
            remoteViews.setViewVisibility(R.id.buttonPause, View.GONE);
            remoteViews.setViewVisibility(R.id.buttonStart, View.GONE);
            remoteViews.setViewVisibility(R.id.buttonResume, View.VISIBLE);
            remoteViews.setOnClickPendingIntent(R.id.buttonResume, getPendingSelfIntent(context, RESUME_TIMER_ACTION));
            remoteViews.setViewVisibility(R.id.buttonStop, View.VISIBLE);
            remoteViews.setOnClickPendingIntent(R.id.buttonStop, getPendingSelfIntent(context, STOP_TIMER_ACTION));
        }
        if (mIsResume) {
            remoteViews.setViewVisibility(R.id.buttonResume, View.GONE);
            remoteViews.setViewVisibility(R.id.buttonStart, View.GONE);
            remoteViews.setViewVisibility(R.id.buttonPause, View.VISIBLE);
            remoteViews.setOnClickPendingIntent(R.id.buttonPause, getPendingSelfIntent(context, PAUSE_TIMER_ACTION));
            remoteViews.setViewVisibility(R.id.buttonStop, View.VISIBLE);
            remoteViews.setOnClickPendingIntent(R.id.buttonStop, getPendingSelfIntent(context, STOP_TIMER_ACTION));
        }
        if (mIsStop) {
            remoteViews.setViewVisibility(R.id.buttonStop, View.GONE);
            remoteViews.setViewVisibility(R.id.buttonResume, View.GONE);
            remoteViews.setViewVisibility(R.id.buttonPause, View.GONE);
            remoteViews.setViewVisibility(R.id.buttonStart, View.VISIBLE);
            remoteViews.setOnClickPendingIntent(R.id.buttonStart, getPendingSelfIntent(context, START_TIMER_ACTION));
        }
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private void onUpdate(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance
                (context);

        // Uses getClass().getName() rather than MyWidget.class.getName() for
        // portability into any App Widget Provider Class
        ComponentName thisAppWidgetComponentName =
                new ComponentName(context.getPackageName(), getClass().getName()
                );
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                thisAppWidgetComponentName);
        onUpdate(context, appWidgetManager, appWidgetIds);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        if (intent.getAction().equals(START_TIMER_ACTION)) {
            mIsStart = true;
            mIsPause = false;
            mIsStop = false;
            mIsResume = false;
            onUpdate(context);
            TimerManager.getInstance(context, HomeWidgetProvider.this).startTimer();

        } else if (intent.getAction().equals(PAUSE_TIMER_ACTION)) {
            mIsStart = false;
            mIsPause = true;
            mIsStop = false;
            mIsResume = false;
            onUpdate(context);
            TimerManager.getInstance(context, HomeWidgetProvider.this).pauseTimer();

        } else if (intent.getAction().equals(RESUME_TIMER_ACTION)) {
            mIsStart = false;
            mIsPause = false;
            mIsStop = false;
            mIsResume = true;
            onUpdate(context);
            TimerManager.getInstance(context, HomeWidgetProvider.this).resumeTimer();
        } else if (intent.getAction().equals(STOP_TIMER_ACTION)) {
            mIsStart = false;
            mIsPause = false;
            mIsResume = false;
            mIsStop = true;
            onUpdate(context);
            TimerManager.getInstance(context, HomeWidgetProvider.this).stopTimer();
            Intent intentStartApp = new Intent(context, MainActivity.class);
            intentStartApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentStartApp);

        } else {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);
            remoteViews.setTextViewText(R.id.textViewTitle, timeElapsedInMillis + "");
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        TimerManager.getInstance(context, HomeWidgetProvider.this).stopTimer();
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void updateTimer(long timeInMillis, Context context) {


        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), HomeWidgetProvider.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        for (int i = 0; i < appWidgetIds.length; i++) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(timeInMillis),
                    TimeUnit.MILLISECONDS.toMinutes(timeInMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeInMillis)),
                    TimeUnit.MILLISECONDS.toSeconds(timeInMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillis)));
            remoteViews.setTextViewText(R.id.textViewTitle, hms);
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }

    }

    @Override
    public void stopTimer(long timeInMillis, Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), HomeWidgetProvider.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        for (int i = 0; i < appWidgetIds.length; i++) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(timeInMillis),
                    TimeUnit.MILLISECONDS.toMinutes(timeInMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeInMillis)),
                    TimeUnit.MILLISECONDS.toSeconds(timeInMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillis)));
            remoteViews.setTextViewText(R.id.textViewTitle, hms);
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
    }

    @Override
    public void resetTimer(long timeInMillis, Context context) {
//        timeElapsedInMillis = 0;

    }
}
