package com.mcorbridge.passwordprotector.error;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mcorbridge.passwordprotector.BaseActivity;
import com.mcorbridge.passwordprotector.MainActivity;
import com.mcorbridge.passwordprotector.R;

import java.util.Calendar;

public class DecipherErrorActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decipher_error);

        applicationModel.setIncorrectDecipherAttempts(applicationModel.getIncorrectDecipherAttempts() + 1);

        final Intent intent = new Intent(this,MainActivity.class);

        // Lockout the user if there are more than 5 attempts at the visual key.
        // Ok, granted, they can erase all the application data and start from ground zero
        // but that gets REALLY tedious because the entire application then needs to be initialized.
        // So, this should deter anyone except the most stupid.
        if(applicationModel.getIncorrectDecipherAttempts() > 5){
            Calendar calendar = Calendar.getInstance();
            //System.out.println("calendar time in millisecs ------->" + calendar.getTimeInMillis());
            setSharedPreferences(String.valueOf(calendar.getTimeInMillis()),"lockout_time");
            new AlertDialog.Builder(this)
                    .setTitle("Alert")
                    .setMessage(R.string.locked_out_msg)
                    .setIcon(R.drawable.alert_icon)
                    .setPositiveButton("Understood", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(intent );
                        }
                    })
                    .show();
        }else{

            new AlertDialog.Builder(this)
                    .setTitle("Alert")
                    .setMessage("There have been\n ------>  " + applicationModel.getIncorrectDecipherAttempts() + "\nattempted logins.\n\nThe app will be locked for 8 hours IF there are more than 5 incorrect login attempts.")
                    .setIcon(R.drawable.alert_icon)
                    .setPositiveButton("Ok, got it.", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(intent );
                        }
                    })
                    .show();
        }

        if(settingsVO.isSound()){
            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.error);
            mediaPlayer.start();
        }


    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
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

    public void setSharedPreferences(String key, String type){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(type, key);  // Saving string
        editor.apply(); // commit changes
    }


}
