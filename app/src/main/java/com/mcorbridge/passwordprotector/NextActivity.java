package com.mcorbridge.passwordprotector;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.mcorbridge.passwordprotector.video.VideoActivity;


public class NextActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        // this value is NOT set to true unless there are zero records in the local database
        applicationModel.setRequestLocalDatabaseRebuild(false);

        Pair pair = getSharedPreferences();

        if(pair.first == null || pair.second == null) {
            System.out.println("You must input your shared preferences!");
            Intent intent = new Intent(this, PassPhraseActivity.class);
            applicationModel.setNewUser(true);
            startActivity(intent);
        }else if(pref.getString("completed_visual_key", null) == null){ // condition where user starts visual key practice, but does not finish
            Intent intent = new Intent(this, VideoActivity.class);
            applicationModel.setNewUser(true);
            startActivity(intent);
        }else{
            System.out.println("Welcome back!");
            applicationModel.setNewUser(false);
            applicationModel.setSecretKey(pair.first.toString());
            applicationModel.setEmail(pair.second.toString());
            // open visual key
            Intent intent = new Intent(this, VisualKeyActivity.class);
            startActivity(intent);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }

    public Pair getSharedPreferences(){
        String secretKey;
        String email;
        secretKey = pref.getString("secret_key", null);
        email = pref.getString("secret_email", null);
        Pair pair = new Pair(secretKey,email);
        return pair;
    }


}
