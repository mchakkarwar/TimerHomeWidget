package com.example.poc_home_widget;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;


/**
 * Created by Mahesh Chakkarwar on 05-07-2016.
 */
public class TimerManager {
    private static TimerManager mTimerManager;
    private Context mContext;
    private long mMillisInFuture = 365 * 24 * 60 * 60 * 1000;
    private long mTimeRemained = 0;
    private long mTimeElapsed = 0;
    private final long INTERVAL = 1000;
    private CountDownTimer mCountDownTimer;
    private TimerCallbacks mTimerCallbacks;

    public static TimerManager getInstance(Context context, TimerCallbacks timerCallbacks) {
        if (mTimerManager == null)
            mTimerManager = new TimerManager(context, timerCallbacks);
        return mTimerManager;
    }

    private TimerManager(final Context mContext, final TimerCallbacks timerCallbacks) {
        this.mContext = mContext;
        this.mTimerCallbacks = timerCallbacks;
    }

    public void startTimer() {
        Log.v("TimerManager", "Timer Started");
        mCountDownTimer = new CountDownTimer(mMillisInFuture, INTERVAL) {
            @Override
            public void onTick(long l) {
                mTimerCallbacks.updateTimer(mTimeElapsed + (mMillisInFuture - l), mContext);
                mTimeRemained = l;
            }

            @Override
            public void onFinish() {
                Log.v("MyCountDownTimer", "Counter finished");
                mTimerCallbacks.stopTimer(0, mContext);
            }
        };
        mCountDownTimer.start();
    }

    public void resumeTimer() {
        Log.v("TimerManager", "Timer Resume");
        mMillisInFuture = mTimeRemained;
        startTimer();
    }

    public void pauseTimer() {
        Log.v("TimerManager", "Timer paused");
        if (mCountDownTimer != null) {
            mTimeElapsed += mMillisInFuture - mTimeRemained;
            mTimerCallbacks.updateTimer(mTimeElapsed, mContext);
            mCountDownTimer.cancel();
        }
    }

    public void stopTimer() {
        Log.v("TimerManager", "Timer stopped");
        mTimeRemained = 0;
        mTimeElapsed = 0;
        mMillisInFuture = 365 * 24 * 60 * 60 * 1000;
        mCountDownTimer.cancel();
        mTimerCallbacks.stopTimer(0, mContext);
    }
}
