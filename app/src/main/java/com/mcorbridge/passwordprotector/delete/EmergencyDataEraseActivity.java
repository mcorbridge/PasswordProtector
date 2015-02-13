package com.mcorbridge.passwordprotector.delete;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.mcorbridge.passwordprotector.BaseActivity;
import com.mcorbridge.passwordprotector.JSON.JsonTask;
import com.mcorbridge.passwordprotector.interfaces.IPasswordActivity;
import com.mcorbridge.passwordprotector.model.ApplicationModel;
import com.mcorbridge.passwordprotector.service.ServletPostAsyncTask;
import com.mcorbridge.passwordprotector.sql.PasswordsDataSource;

public class EmergencyDataEraseActivity extends BaseActivity implements IPasswordActivity {

    private ApplicationModel applicationModel;
    private SharedPreferences pref;
    private PasswordsDataSource passwordsDataSource;
    private Context context;

    public EmergencyDataEraseActivity(Context context){
        applicationModel = ApplicationModel.getInstance();
        passwordsDataSource = new PasswordsDataSource(context);
        pref = context.getSharedPreferences("PasswordProtector", MODE_PRIVATE);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    public void eraseSharedPreferences(){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("secret_key", null);
        editor.putString("sound_setting", null);
        editor.putString("secret_email", null);
        editor.putString("completed_visual_key", null);
        editor.apply();
    }

    public void eraseLocalDataStore(){
        passwordsDataSource.open();
        passwordsDataSource.deleteLocalPasswordData();
        passwordsDataSource.close();
    }

    public void eraseLocalMemoryStore(){
        applicationModel.setDecipheredPasswordDataVOs(null);
    }

    public void eraseCloudStore()throws  Exception{
        String jsonRequest = JsonTask.createDeleteAllJSON();
        new ServletPostAsyncTask().execute(new Pair<Context, String>(context, jsonRequest));
    }

    public void processResults(String results){
        System.out.println("--------------> processResults " + results);
    }

    public void signOut(){
        System.out.println("--------------> signOut");
    }

    public void showTimeoutWarning(){
        System.out.println("--------------> showTimeoutWarning");
    }
}
