package com.mcorbridge.passwordprotector;

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

                if(progress < 30){
                    doNext();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.whoosh);
        mediaPlayer.start();

        Intent intent = new Intent(this, NextActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.getItem(ApplicationConstants.MENU_ITEM_TEST).setVisible(false);
        menu.getItem(ApplicationConstants.MENU_ITEM_HOME).setVisible(false);
        menu.getItem(ApplicationConstants.MENU_ITEM_CREATE).setVisible(false);
        menu.getItem(ApplicationConstants.MENU_ITEM_READ).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }



}
