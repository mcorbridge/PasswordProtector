package com.mcorbridge.passwordprotector.video;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import com.mcorbridge.passwordprotector.BaseActivity;
import com.mcorbridge.passwordprotector.R;
import com.mcorbridge.passwordprotector.constants.ApplicationConstants;

import java.util.Timer;
import java.util.TimerTask;

public class VideoActivity extends BaseActivity {

    private Uri[] uris = {
                            Uri.parse("android.resource://com.mcorbridge.passwordprotector/raw/test"),
                            Uri.parse("android.resource://com.mcorbridge.passwordprotector/raw/secondpass")
                         };

    private TextView textView;
    private VideoView videoView;
    private int ndx;
    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        textView = (TextView)findViewById(R.id.textView);

        videoView = (VideoView)findViewById(R.id.videoView);
        videoView.setVideoURI(uris[ndx]);
        videoView.setVisibility(View.INVISIBLE);
        videoView.setOnCompletionListener(onCompletionListener);

        startTimer();
    }

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            ndx++;
            if(ndx == uris.length){
                videoView.stopPlayback();
                stoptimertask();
                return;
            }

            videoView.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);

            startTimer();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.getItem(ApplicationConstants.MENU_ITEM_TEST).setVisible(false);
        menu.getItem(ApplicationConstants.MENU_ITEM_HOME).setVisible(true);
        menu.getItem(ApplicationConstants.MENU_ITEM_CREATE).setVisible(false);
        menu.getItem(ApplicationConstants.MENU_ITEM_READ).setVisible(false);
        menu.getItem(ApplicationConstants.MENU_ITEM_VIDEO).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }

    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 10000, 100); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        videoView.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.INVISIBLE);
                        videoView.setVideoURI(uris[ndx]);
                        videoView.start();
                        stoptimertask();
                    }
                });
            }
        };
    }
}
