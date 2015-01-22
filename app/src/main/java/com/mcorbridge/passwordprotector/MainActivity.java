package com.mcorbridge.passwordprotector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import com.mcorbridge.passwordprotector.animation.GestureListener;


public class MainActivity extends Activity {

    GestureDetector gestureDetector = new GestureDetector(new GestureListener(this));
    private static final String SWIPE_RIGHT = "swipeRight";
    private static final String SWIPE_LEFT = "swipeLeft";
    SeekBar seekBar;

    @Override
    protected void onStart()
    {
        super.onStart();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> onStart");
    }
    @Override
    protected void onStop(){
        super.onStop();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> onStop");
        seekBar.setProgress(100);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> onCreate");

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

                if(progress < 10){
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

    public void doFling(String swipeDirection){
        if(swipeDirection.equals(SWIPE_LEFT)){
            Intent intent = new Intent(this, NextActivity.class);
            startActivity(intent);
        }
    }

    public void doNext(){
        Intent intent = new Intent(this, NextActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
