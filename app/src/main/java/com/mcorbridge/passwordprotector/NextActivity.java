package com.mcorbridge.passwordprotector;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.mcorbridge.passwordprotector.model.ApplicationModel;


public class NextActivity extends Activity {

    public SharedPreferences pref;

    private ApplicationModel applicationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        applicationModel = ApplicationModel.getInstance();

        pref = getApplicationContext().getSharedPreferences("PasswordProtector", MODE_PRIVATE);

        Pair pair = getSharedPreferences();

        if(pair.first == null || pair.second == null){
            System.out.println("You must input your shared preferences!");
            Intent intent = new Intent(this, PassPhraseActivity.class);
            startActivity(intent);
        }else{
            System.out.println("Welcome back!");
            applicationModel.setSecretKey(pair.first.toString());
            applicationModel.setEmail(pair.second.toString());
            // open visual key
            Intent intent = new Intent(this, VisualKeyActivity.class);
            startActivity(intent);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_next, menu);
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

    public Pair getSharedPreferences(){
        String secretKey;
        String email;
        secretKey = pref.getString("secret_key", null);
        email = pref.getString("secret_email", null);
        Pair pair = new Pair(secretKey,email);
        return pair;
    }


}
