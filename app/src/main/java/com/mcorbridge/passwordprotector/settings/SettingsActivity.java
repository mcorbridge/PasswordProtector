package com.mcorbridge.passwordprotector.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.mcorbridge.passwordprotector.BaseActivity;
import com.mcorbridge.passwordprotector.MainActivity;
import com.mcorbridge.passwordprotector.R;
import com.mcorbridge.passwordprotector.constants.ApplicationConstants;
import com.mcorbridge.passwordprotector.delete.EmergencyDataEraseActivity;

public class SettingsActivity extends BaseActivity {

    private ToggleButton toggleButtonSound;
    EmergencyDataEraseActivity emergencyDataEraseActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearLayoutMain);
        View root = linearLayout.getRootView();
        root.setBackgroundColor(getResources().getColor(android.R.color.white));

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        toggleButtonSound = (ToggleButton)findViewById(R.id.toggleButtonSound);

        emergencyDataEraseActivity = new EmergencyDataEraseActivity(this);

        doSoundSetting();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.getItem(ApplicationConstants.MENU_ITEM_TEST).setVisible(false);
        menu.getItem(ApplicationConstants.MENU_ITEM_HOME).setVisible(true);
        menu.getItem(ApplicationConstants.MENU_ITEM_CREATE).setVisible(false);
        menu.getItem(ApplicationConstants.MENU_ITEM_READ).setVisible(false);
        menu.getItem(ApplicationConstants.MENU_ITEM_VIDEO).setVisible(false);
        menu.getItem(ApplicationConstants.MENU_ITEM_SETTINGS).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

    private void doSoundSetting(){

       if(settingsVO.isSound()){
           toggleButtonSound.setChecked(true);
       }else{
           toggleButtonSound.setChecked(false);
       }

    }

    public void doSoundClick(View v){
        SharedPreferences.Editor editor = pref.edit();

        if(toggleButtonSound.isChecked()){
            editor.putString(ApplicationConstants.SOUND_SETTING, "true");
            settingsVO.setSound(true);
        }else{
            editor.putString(ApplicationConstants.SOUND_SETTING, "false");
            settingsVO.setSound(false);
        }

        editor.apply(); // commit changes
    }

    public void doSyncClick(View v){
        emergencyDataEraseActivity.eraseLocalMemoryStore();
        emergencyDataEraseActivity.eraseLocalDataStore();
        goToMainActivity();
    }

    public void doEmergencyEraseClick(View v){
        //set activity background red
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearLayoutMain);
        View root = linearLayout.getRootView();
        root.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));

        new AlertDialog.Builder(this)
                .setTitle("ALERT!")
                .setMessage("IF YOU PRESS 'CONTINUE' ALL YOUR DATA WILL BE LOST FOREVER")
                .setIcon(R.drawable.alert_icon)
                .setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        doLastChance();
                    }
                })
                .setNegativeButton("STOP", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearLayoutMain);
                        View root = linearLayout.getRootView();
                        root.setBackgroundColor(getResources().getColor(android.R.color.white));
                    }
                })
                .show();
    }

    private void doLastChance(){
        new AlertDialog.Builder(this)
                .setTitle("LAST CHANCE!")
                .setMessage("ARE YOU SURE THIS IS WHAT YOU WANT TO DO?")
                .setIcon(R.drawable.alert_icon)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            emergencyDataEraseActivity.eraseCloudStore();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        emergencyDataEraseActivity.eraseLocalDataStore();
                        emergencyDataEraseActivity.eraseLocalMemoryStore();
                        emergencyDataEraseActivity.eraseSharedPreferences();

                        goToMainActivity();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearLayoutMain);
                        View root = linearLayout.getRootView();
                        root.setBackgroundColor(getResources().getColor(android.R.color.white));
                    }
                })
                .show();
    }

    private void goToMainActivity(){
        startActivity(new Intent(this, MainActivity.class));
    }
}
