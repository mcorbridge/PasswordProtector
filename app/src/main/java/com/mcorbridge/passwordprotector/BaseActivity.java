package com.mcorbridge.passwordprotector;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mcorbridge.passwordprotector.create.CreateActivity;
import com.mcorbridge.passwordprotector.model.ApplicationModel;
import com.mcorbridge.passwordprotector.read.ReadActivity;
import com.mcorbridge.passwordprotector.settings.SettingsActivity;
import com.mcorbridge.passwordprotector.timeout.TimeOut;
import com.mcorbridge.passwordprotector.video.VideoActivity;
import com.mcorbridge.passwordprotector.vo.SettingsVO;


public class BaseActivity extends Activity {

    public ApplicationModel applicationModel = ApplicationModel.getInstance();

    public TimeOut timeOut = TimeOut.getInstance();

    public SharedPreferences pref;

    public SettingsVO settingsVO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        pref = getApplicationContext().getSharedPreferences("PasswordProtector", MODE_PRIVATE);

        settingsVO = applicationModel.getSettingsVO();

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

        Intent intent;

        switch(id){
            case R.id.action_test:
                intent = new Intent(this,TestActivity.class);
                startActivity(intent);
            break;

            case R.id.action_home:
                intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                break;

            case R.id.action_create:
                intent = new Intent(this,CreateActivity.class);
                startActivity(intent);
                break;

            case R.id.action_read:
                intent = new Intent(this,ReadActivity.class);
                startActivity(intent);
                break;

            case R.id.action_video:
                intent = new Intent(this,VideoActivity.class);
                startActivity(intent);
                break;

            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }



        return super.onOptionsItemSelected(item);
    }


}
