package com.mcorbridge.passwordprotector;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mcorbridge.passwordprotector.JSON.JsonTask;
import com.mcorbridge.passwordprotector.model.ApplicationModel;
import com.mcorbridge.passwordprotector.sql.Password;
import com.mcorbridge.passwordprotector.sql.PasswordsDataSource;

import java.util.List;


public class TestActivity extends Activity {

    private ApplicationModel applicationModel;
    private PasswordsDataSource passwordsDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        applicationModel = ApplicationModel.getInstance();

        // for offline data work
        passwordsDataSource = new PasswordsDataSource(getApplicationContext());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
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

    public void readFromLocalDatabase(View v){
        passwordsDataSource.open();
        List<Password> passwords = passwordsDataSource.getAllPasswords();
        passwordsDataSource.close();
    }

    // set modified to 1 indicating that this value needs to be synchronized with the cloud
    public void addToLocalDatabase1(View v) throws Exception{
            passwordsDataSource.open();
            String action = "create";
            String category = "category";
            String title = "title";
            String value = "value";
            String name = "mikecorbridge@gmail.com";
            passwordsDataSource.createPassword(JsonTask.createRndID(),action,category,1,name,title,value);
            passwordsDataSource.close();
    }

    // set modified to 0 indicating that this value does NOT need to be synchronized with the cloud
    public void addToLocalDatabase0(View v) throws Exception{
        passwordsDataSource.open();
        String action = "create";
        String category = "category";
        String title = "title";
        String value = "value";
        String name = "mikecorbridge@gmail.com";
        passwordsDataSource.createPassword(JsonTask.createRndID(),action,category,0,name,title,value);
        passwordsDataSource.close();
    }

    public void clearDatabase(View v){
        System.out.println("**************************** delete all values in local database ****************************");
        passwordsDataSource.open();
        passwordsDataSource.deleteLocalPasswordData();
        passwordsDataSource.close();
    }

}
