package com.mcorbridge.passwordprotector.timeout;

import android.content.Context;
import android.os.Handler;

import com.mcorbridge.passwordprotector.constants.ApplicationConstants;
import com.mcorbridge.passwordprotector.interfaces.IPasswordActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Mike on 2/7/2015.
 * copyright Michael D. Corbridge
 */
public class TimeOut {

    private Timer timer;
    TimerTask timerTask;
    private final Handler handler = new Handler();

    private TimeOut() {

    }


    /**
     * Initializes singleton.
     *
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        private static final TimeOut INSTANCE = new TimeOut();
    }

    public static TimeOut getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static Context context;

    private int ndx;

    public void setContext(Context context){
        this.context = context;
    }



    public void showWarning(){
        System.out.println("------------- 1 min time out warning -----------------");
        IPasswordActivity activity = (IPasswordActivity)this.context;
        activity.showTimeoutWarning();
    }

    public void startTimer() {
        if (timer != null) {
            return;
        }

        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 0, 1000); //
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {

            public void run() {

                handler.post(new Runnable() {
                    public void run() {
                        ndx++;
                        if(ndx == ApplicationConstants.SECONDS_PER_FOUR_MIN){
                            showWarning();
                        }
                        else if(ndx > ApplicationConstants.SECONDS_PER_FIVE_MIN){
                            doTimeOutAlert();
                            stopTimerTask();
                        }
                    }
                });
            }
        };
    }

    public void stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
            ndx = 0;
        }
    }

    private void doTimeOutAlert(){
        System.out.println("------------- app time out -----------------");
        IPasswordActivity activity = (IPasswordActivity)this.context;
        activity.signOut();
    }
}
