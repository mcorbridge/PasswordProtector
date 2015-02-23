package com.mcorbridge.passwordprotector;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import com.mcorbridge.passwordprotector.animation.GestureListener;
import com.mcorbridge.passwordprotector.constants.ApplicationConstants;


public class MainActivity extends BaseActivity {

    GestureDetector gestureDetector = new GestureDetector(new GestureListener(this));
    private static final String SWIPE_RIGHT = "swipeRight";
    private static final String SWIPE_LEFT = "swipeLeft";
    SeekBar seekBar;

    @Override
    protected void onStart()
    {
        super.onStart();
        timeOut.stopTimerTask();
    }

    @Override
    protected void onStop(){
        super.onStop();
        seekBar.setProgress(100);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        // DEV mode
        applicationModel.setDevMode(false); //todo this MUST be set to false before going live !!!!

        View v = findViewById(R.id.main);
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return false;
                } else {
                    return true;
                }
            }

        });

        seekBar = (SeekBar)findViewById(R.id.myseek);
        seekBar.setProgress(100);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(seekBar.getProgress() == 0){
                  doNext();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                doThumbAnimation();
            }
        });

        if(applicationModel.isDevMode()){
            new AlertDialog.Builder(this)
                    .setTitle("OMG!")
                    .setMessage("The application is in dev mode")
                    .setIcon(R.drawable.alert_icon)
                    .setPositiveButton("Yikes!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // stub
                        }
                    })
                    .show();
        }

        applicationModel.setTimeoutAware(false);

        applicationSettings();
    }

    private void applicationSettings(){
        String soundSetting = pref.getString(ApplicationConstants.SOUND_SETTING, null);
        if(soundSetting != null){
            if(soundSetting.equals("true")){
                settingsVO.setSound(true);
            }else{
                settingsVO.setSound(false);
            }
        }else{
            settingsVO.setSound(true);
        }

    }

    private void doThumbAnimation(){
        ValueAnimator anim = ValueAnimator.ofInt(seekBar.getProgress(), 0);
        anim.setDuration(100);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animProgress = (Integer) animation.getAnimatedValue();
                seekBar.setProgress(animProgress);
            }
        });

        anim.start();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

    public void doFling(String swipeDirection){
        if(swipeDirection.equals(SWIPE_LEFT)){
            Intent intent = new Intent(this, NextActivity.class);
            startActivity(intent);
        }
    }

    public void doNext(){
        if(settingsVO.isSound()){
            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.whoosh);
            mediaPlayer.start();
        }
        Intent intent = new Intent(this, NextActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.getItem(ApplicationConstants.MENU_ITEM_TEST).setVisible(true);
        menu.getItem(ApplicationConstants.MENU_ITEM_HOME).setVisible(false);
        menu.getItem(ApplicationConstants.MENU_ITEM_CREATE).setVisible(false);
        menu.getItem(ApplicationConstants.MENU_ITEM_READ).setVisible(false);
        menu.getItem(ApplicationConstants.MENU_ITEM_VIDEO).setVisible(false);
        menu.getItem(ApplicationConstants.MENU_ITEM_SETTINGS).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }



}
