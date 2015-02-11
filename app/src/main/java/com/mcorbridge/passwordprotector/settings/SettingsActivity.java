package com.mcorbridge.passwordprotector.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

import com.mcorbridge.passwordprotector.BaseActivity;
import com.mcorbridge.passwordprotector.MainActivity;
import com.mcorbridge.passwordprotector.R;
import com.mcorbridge.passwordprotector.constants.ApplicationConstants;

public class SettingsActivity extends BaseActivity {

    private ToggleButton toggleButtonSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        toggleButtonSound = (ToggleButton)findViewById(R.id.toggleButtonSound);

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
}
