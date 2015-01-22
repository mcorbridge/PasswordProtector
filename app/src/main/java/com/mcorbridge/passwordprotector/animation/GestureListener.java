package com.mcorbridge.passwordprotector.animation;

import android.app.Activity;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.mcorbridge.passwordprotector.MainActivity;

/**
 * Created by Mike on 1/17/2015.
 * copyright Michael D. Corbridge
 */
public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private static final String SWIPE_RIGHT = "swipeRight";
    private static final String SWIPE_LEFT = "swipeLeft";
    private Context context;

    public GestureListener(Context context){
        this.context = context;
    }

    public void parentCallback( String swipeDirection){
        final Activity main = (Activity)context;
        final String directionSwipe = swipeDirection;
        ((MainActivity) main).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity) main).doFling(directionSwipe);
            }
        });
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
            return false;
        // right to left swipe
        if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            parentCallback(SWIPE_LEFT);
        }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            parentCallback(SWIPE_RIGHT);
        }

        return true;
    }
}


