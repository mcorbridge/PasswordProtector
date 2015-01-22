package com.mcorbridge.passwordprotector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mcorbridge.passwordprotector.create.CreateActivity;
import com.mcorbridge.passwordprotector.model.ApplicationModel;
import com.mcorbridge.passwordprotector.read.ReadActivity;

import java.lang.reflect.Method;


public class PasswordDataActivity extends Activity{


    private ApplicationModel applicationModel;
    private String currentActivity;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_data);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        applicationModel = ApplicationModel.getInstance();

        TextView textView = (TextView)findViewById(R.id.textViewTitle);
        textView.setText(applicationModel.getEmail() + "\n\n" + applicationModel.getCipher());

        context = getApplicationContext();

        //offline-online mode check for wifi and/or data over telecom
        checkCommunications();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_read_password, menu);
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

    public void alertServletEndpoint(String servletEndPoint){
        System.out.println(servletEndPoint);
    }

    /**
     ****************************************************************************
     * post json strings to servlet
     */
    public void doCreate(View v){
        currentActivity = "create";
        Intent intent = new Intent(this, CreateActivity.class);
        startActivity(intent);
    }

    /**
     *
     */
    public void doRead(View v){
        currentActivity = "read";
        Intent intent = new Intent(this, ReadActivity.class);
        startActivity(intent);
    }

    /**
     *
     */
    public void doUpdate(View v){
        currentActivity = "update";
        //Intent intent = new Intent(this, UpdateActivity.class);
        //startActivity(intent);
    }

    /**
     *
     */
    public void doDelete(View v){
        currentActivity = "delete";
        //Intent intent = new Intent(this, DeleteActivity.class);
        //startActivity(intent);
    }

    public void checkCommunications(){
        applicationModel.setIsDataConnected(true);
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        boolean mobileDataEnabled = false; // Assume disabled
        try {
            Class cmClass = Class.forName(connManager.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean)method.invoke(connManager);
        } catch (Exception e) {
            // Some problem accessible private API
            System.out.println(e.getStackTrace());
        }

        // go to offline mode IF there is no data connection
        if(!netInfo.isConnected() && !mobileDataEnabled){
            applicationModel.setIsDataConnected(false);
            new AlertDialog.Builder(this)
                    .setTitle("Alert")
                    .setMessage("You do not have a data connection.\nThe application will continue in offline mode.\nAll offline changes will be synchronized with the cloud data.")
                    .setIcon(R.drawable.alert_icon)
                    .setPositiveButton("Ok, got it", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // stub
                        }
                    })
                    .show();
        }
    }


}
