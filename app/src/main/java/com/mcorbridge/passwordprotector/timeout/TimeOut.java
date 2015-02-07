package com.mcorbridge.passwordprotector.timeout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;

import com.mcorbridge.passwordprotector.R;
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



    public void showWarning(String screen){
        new AlertDialog.Builder(this.context)
                .setTitle("Alert")
                .setMessage("The application is about to timeout from " + screen)
                .setIcon(R.drawable.alert_icon)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
// stub
                    }
                })
                .show();
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
                        System.out.println("----------> " + ndx++ + " seconds");
                        if(ndx > ApplicationConstants.SECONDS_PER_FIVE_MIN){
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
        IPasswordActivity activity = (IPasswordActivity)this.context;
        activity.signOut();
    }
}
