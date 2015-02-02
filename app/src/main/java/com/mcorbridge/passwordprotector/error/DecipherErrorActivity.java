package com.mcorbridge.passwordprotector.error;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mcorbridge.passwordprotector.BaseActivity;
import com.mcorbridge.passwordprotector.MainActivity;
import com.mcorbridge.passwordprotector.R;
import com.mcorbridge.passwordprotector.model.ApplicationModel;

import java.util.Calendar;

public class DecipherErrorActivity extends BaseActivity {

    private ApplicationModel applicationModel = ApplicationModel.getInstance();
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decipher_error);

        pref = getApplicationContext().getSharedPreferences("PasswordProtector", MODE_PRIVATE);

        applicationModel.setIncorrectDecipherAttempts(applicationModel.getIncorrectDecipherAttempts() + 1);

        //lockout the user if there are greater than 5 attempts at the visual key
        if(applicationModel.getIncorrectDecipherAttempts() > 5){
            Calendar calendar = Calendar.getInstance();
            System.out.println(calendar.getTimeInMillis());
            setSharedPreferences(String.valueOf(calendar.getTimeInMillis()),"lockout_time");
        }else{
            final Intent intent = new Intent(this,MainActivity.class);
            new AlertDialog.Builder(this)
                    .setTitle("Alert")
                    .setMessage("There have been " + applicationModel.getIncorrectDecipherAttempts() + "attempted logins.\n\nThe app will locked for 8hrs. if there are more than 5 incorrect login attempts.")
                    .setIcon(R.drawable.alert_icon)
                    .setPositiveButton("Ok, got it.", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(intent );
                        }
                    })
                    .show();
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

    public void setSharedPreferences(String key, String type){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(type, key);  // Saving string
        editor.apply(); // commit changes
    }


}
