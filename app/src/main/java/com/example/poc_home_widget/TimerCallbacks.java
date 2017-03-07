package com.example.poc_home_widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;

/**
 * Created by Mahesh Chakkarwar on 27-10-2016.
 */

public interface TimerCallbacks {
    public void updateTimer(long timeInMillis,Context context);

    public void stopTimer(long timeInMillis, Context context);

    public void resetTimer(long timeInMillis, Context context);
}
