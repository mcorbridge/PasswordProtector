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

import java.util.Calendar;

public class DecipherErrorActivity extends BaseActivity {

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decipher_error);

        pref = getApplicationContext().getSharedPreferences("PasswordProtector", MODE_PRIVATE);

        applicationModel.setIncorrectDecipherAttempts(applicationModel.getIncorrectDecipherAttempts() + 1);

        final Intent intent = new Intent(this,MainActivity.class);

        //lockout the user if there are greater than 5 attempts at the visual key
        if(applicationModel.getIncorrectDecipherAttempts() > 5){
            Calendar calendar = Calendar.getInstance();
            System.out.println(calendar.getTimeInMillis());
            setSharedPreferences(String.valueOf(calendar.getTimeInMillis()),"lockout_time");
            new AlertDialog.Builder(this)
                    .setTitle("Alert")
                    .setMessage("The PasswordProtector application is now locked for 8 hours.\nAn email has been sent to the account owner")
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
                    .setMessage("There have been\n ------>  " + applicationModel.getIncorrectDecipherAttempts() + "\nattempted logins.\n\nThe app will locked for 8 hrs. IF there are more than 5 incorrect login attempts.")
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
