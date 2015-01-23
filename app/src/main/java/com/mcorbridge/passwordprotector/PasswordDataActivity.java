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

import com.mcorbridge.passwordprotector.create.CreateActivity;
import com.mcorbridge.passwordprotector.interfaces.IPasswordActivity;
import com.mcorbridge.passwordprotector.model.ApplicationModel;
import com.mcorbridge.passwordprotector.read.ReadActivity;
import com.mcorbridge.passwordprotector.sql.Password;
import com.mcorbridge.passwordprotector.sql.PasswordsDataSource;
import com.mcorbridge.passwordprotector.sql.SQLActivity;
import com.mcorbridge.passwordprotector.vo.PasswordDataVO;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class PasswordDataActivity extends Activity implements IPasswordActivity{


    private ApplicationModel applicationModel;
    private PasswordsDataSource passwordsDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_data);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        applicationModel = ApplicationModel.getInstance();

        // for offline data work
        passwordsDataSource = new PasswordsDataSource(getApplicationContext());

        //TextView textView = (TextView)findViewById(R.id.textViewTitle);
        //textView.setText(applicationModel.getEmail() + "\n\n" + applicationModel.getCipher());

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
        Intent intent = new Intent(this, CreateActivity.class);
        startActivity(intent);
    }

    /**
     *
     */
    public void doRead(View v){
        Intent intent = new Intent(this, ReadActivity.class);
        startActivity(intent);
    }

    /**
     *
     */
    public void doUpdate(View v){
        //Intent intent = new Intent(this, UpdateActivity.class);
        //startActivity(intent);
    }

    /**
     *
     */
    public void doDelete(View v){
        //Intent intent = new Intent(this, DeleteActivity.class);
        //startActivity(intent);
    }

    /**
     * this application can run in an offline mode IF there is no mobile data communications (wifi or mobile data)
     * of course, I will need to add all the sql to handle this (yuk!)
     */
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
            final Intent intent = new Intent(this, SQLActivity.class);
            new AlertDialog.Builder(this)
                    .setTitle("Alert")
                    .setMessage("You do not have a data connection.\nThe application will continue in offline mode.\nAll offline changes will be synchronized with the cloud when you next connect.")
                    .setIcon(R.drawable.alert_icon)
                    .setPositiveButton("Ok, got it", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //startActivity(intent);
                        }
                    })
                    .show();
        }else{
            // look for any modified password data in the local db and synchronize with cloud
            List<Password> passwords = readFromLocalDatabase();
            List<PasswordDataVO> passwordDataVOs = findModifiedPasswordDataVOs(passwords);
            // persist these local passwordDataVOs to the cloud
            createCloudPasswordData(passwordDataVOs);
        }
    }

    private List<Password> readFromLocalDatabase(){
        passwordsDataSource.open();
        List<Password> passwords = passwordsDataSource.getAllPasswords();
        passwordsDataSource.close();
        return passwords;
    }

    private List<PasswordDataVO> findModifiedPasswordDataVOs(List<Password> passwords){
        Iterator iterator = passwords.iterator();
        List<PasswordDataVO> passwordDataVOs = new ArrayList<PasswordDataVO>();
        while(iterator.hasNext()){
            Password password = (Password)iterator.next();
            if(password.isModified()){
                PasswordDataVO passwordDataVO = new PasswordDataVO();
                passwordDataVO.setCategory(password.getCategory());
                passwordDataVO.setName(password.getName());
                passwordDataVO.setTitle(password.getTitle());
                passwordDataVO.setAction(password.getAction());
                passwordDataVO.setValue(password.getValue());
                passwordDataVOs.add(passwordDataVO);
            }
        }
        return passwordDataVOs;
    }

    private void createCloudPasswordData(List<PasswordDataVO> passwordDataVOs){
        Iterator iterator = passwordDataVOs.iterator();
        while (iterator.hasNext()){
            PasswordDataVO passwordDataVO = (PasswordDataVO)iterator.next();
            //todo not exactly sure how I accomplish this if there are multiple passwordData objects that were modified
        }

    }

    public void processResults(String results){
        System.out.println(results);
    }
}
