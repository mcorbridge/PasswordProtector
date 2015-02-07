package com.mcorbridge.passwordprotector.video;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.VideoView;

import com.mcorbridge.passwordprotector.BaseActivity;
import com.mcorbridge.passwordprotector.R;
import com.mcorbridge.passwordprotector.constants.ApplicationConstants;
import com.mcorbridge.passwordprotector.practice.PracticeActivity;

import java.util.Timer;
import java.util.TimerTask;

public class VideoActivity extends BaseActivity {

    private Uri[] uris = {
                            Uri.parse("android.resource://com.mcorbridge.passwordprotector/raw/firstpass"),
                            Uri.parse("android.resource://com.mcorbridge.passwordprotector/raw/secondpass")
                         };

    private String[] titles =   {
                                "Creating your private 'Visual Password'.\n\nThe following screen demonstrates how you use Drag/Drop to place the colored " +
                                "squares on a target.\n\nIt is important to note that both the square color AND the sequence in which they are placed, are " +
                                "used to create your unique 'Visual Password'.\n\n",

                                "The next screen illustrates the importance of the sequence in which the colored squares are placed.\n\nNote that although " +
                                "the color pattern is identical to the demo in the first screen, the 'Visual Password' created in this instance is completely " +
                                "different because the Drag/Drop sequence is different.",

                                "Now it's your turn to create your own 'Visual Password'.\n\n It is vitally important that you remember this pattern. " +
                                "You will need to complete the SAME pattern 10 times before moving on to the next phase where you will be creating and saving your " +
                                "encrypted information.  Once you create this pattern, it will be used to retrieve your encrypted passwords."
                               };

    private TextView textView;
    private VideoView videoView;
    private int ndx;
    Timer timer;
    TimerTask timerTask;
    private final Handler handler = new Handler();
    private static final String IN = "in";
    private static final String OUT = "out";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        textView = (TextView)findViewById(R.id.textView);
        textView.setText(titles[ndx]);

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
        videoView.setVisibility(View.INVISIBLE);
        textView.setText(titles[ndx]);
        textView.setVisibility(View.VISIBLE);

        textFade(IN);

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

                if(ndx == titles.length-1){
                    startPracticeIntent();
                    stoptimertask();
                    return;
                }

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

    private void startPracticeIntent(){
        startActivity(new Intent(this,PracticeActivity.class));
    }

    private void textFade(String direction){
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
        AlphaAnimation fadeOut = new AlphaAnimation( 1.0f , 0.0f ) ;
        if(direction == IN){
            textView.startAnimation(fadeIn);
        }else{
            textView.startAnimation(fadeOut);
        }

        fadeIn.setDuration(1000);
        fadeIn.setFillAfter(true);
        fadeOut.setDuration(1000);
        fadeOut.setFillAfter(true);
        fadeOut.setStartOffset(8000+fadeIn.getStartOffset());

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textFade(OUT);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }
}
