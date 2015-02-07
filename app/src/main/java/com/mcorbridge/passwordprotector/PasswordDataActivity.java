package com.mcorbridge.passwordprotector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mcorbridge.passwordprotector.JSON.JsonTask;
import com.mcorbridge.passwordprotector.create.CreateActivity;
import com.mcorbridge.passwordprotector.interfaces.IPasswordActivity;
import com.mcorbridge.passwordprotector.read.ReadActivity;
import com.mcorbridge.passwordprotector.service.ServletPostAsyncTask;
import com.mcorbridge.passwordprotector.sql.Password;
import com.mcorbridge.passwordprotector.sql.PasswordsDataSource;
import com.mcorbridge.passwordprotector.vo.PasswordDataVO;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class PasswordDataActivity extends BaseActivity implements IPasswordActivity{

    private PasswordsDataSource passwordsDataSource;
    private List<PasswordDataVO> passwordDataVOs;
    Iterator pIterator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_data);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        // for offline data work
        passwordsDataSource = new PasswordsDataSource(getApplicationContext());

        //offline-online mode check for wifi and/or data over telecom
        checkMobileDataConnectivity();

        //take user directly to the read activity
        Intent intent = new Intent(this, ReadActivity.class);
        startActivity(intent);

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
     */
    public void checkMobileDataConnectivity(){
        applicationModel.setIsDataConnected(true); //default state
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        // supplicant state (http://stackoverflow.com/questions/3841317/how-to-see-if-wifi-is-connected-in-android/3841444#3841444)
        boolean isWifiAuthenticated = false;
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        SupplicantState supState = wifiInfo.getSupplicantState();
        if(supState == supState.ASSOCIATED){
            isWifiAuthenticated = true;
        }

        boolean mobileDataEnabled = false;
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
        if(!netInfo.isConnected() && !mobileDataEnabled && !isWifiAuthenticated){
            applicationModel.setIsDataConnected(false);

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
            findModifiedPasswordDataVOs(passwords);
            // persist these local passwordDataVOs to the cloud
            try {
                createCloudPasswordData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<Password> readFromLocalDatabase(){
        passwordsDataSource.open();
        List<Password> passwords = passwordsDataSource.getAllPasswords();
        passwordsDataSource.close();
        return passwords;
    }

    /**
     * Note that the 'password' data object is different from the 'PasswordDataVO'
     * The 'password' is ONLY created in the offline mode, and contains a int=1 'modified' that indicates
     * that this data must be synchronized with the cloud data when the app next goes online
     * if int modified = 0, then the password will NOT be synchronized with the cloud version
     * @param passwords
     * @return
     */
    private void findModifiedPasswordDataVOs(List<Password> passwords){
        Iterator iterator = passwords.iterator();
        passwordDataVOs = new ArrayList<PasswordDataVO>();
        while(iterator.hasNext()){
            Password password = (Password)iterator.next();
            if(password.isModified() == 1){ // this is the 'modified' I was talking about
                PasswordDataVO passwordDataVO = new PasswordDataVO();
                passwordDataVO.setId(password.getPswdID()); //watch out for this (and it is my fault) the id IS NOT the same as pswdID <- my bad
                passwordDataVO.setCategory(password.getCategory());
                passwordDataVO.setName(password.getName());
                passwordDataVO.setTitle(password.getTitle());
                passwordDataVO.setAction(password.getAction());
                passwordDataVO.setValue(password.getValue());
                passwordDataVOs.add(passwordDataVO);

                //update the password data in the local database and set password isModified to 0 !!!!
                setPasswordNotModified(password);
            }
        }

        // pIterator is used to send any data that has been created offline to the cloud
        if(passwordDataVOs.size() > 0){
            pIterator = passwordDataVOs.iterator();
        }

        if(getNumValuesInLocalDatabase() == 0){
            System.out.println("****************************  local database is empty ****************************");
            System.out.println("                              set rebuild flag to TRUE                            ");
            applicationModel.setRequestLocalDatabaseRebuild(true); // flag the app to rebuild the local database
        }
    }

    private void setPasswordNotModified(Password password){
        passwordsDataSource.open();
        passwordsDataSource.setPasswordModifiedState(password, 0);
        passwordsDataSource.close();
    }

    // clean up operation
    // user will have the option of cleaning out the local db and creating a clean copy by synchronizing with cloud values
    public void cleanOutLocalDatabaseFlagForRebuild(){
        passwordsDataSource.open();
        passwordsDataSource.deleteLocalPasswordData();
        passwordsDataSource.close();
        applicationModel.setRequestLocalDatabaseRebuild(true); // flag the app to rebuild the local database
    }

    public int getNumValuesInLocalDatabase(){
        passwordsDataSource.open();
        List<Password> passwords = passwordsDataSource.getAllPasswords();
        passwordsDataSource.close();
        return passwords.size();
    }

    private void createCloudPasswordData() throws Exception{
        if(pIterator == null)
            return;
        PasswordDataVO passwordDataVO = (PasswordDataVO)pIterator.next();
        // note that the data is already encrypted
        String jsonRequest = JsonTask.createPreEncryptedJSON(
                passwordDataVO.getCategory(),
                passwordDataVO.getTitle(),
                passwordDataVO.getValue(),
                passwordDataVO.getAction(),
                passwordDataVO.getId());
        System.out.println("********************* cloud synchronization *********************");
        System.out.println(jsonRequest);
        postToServlet(jsonRequest);
    }

    private void postToServlet(String jsonRequest) throws  Exception{
        new ServletPostAsyncTask().execute(new Pair<Context, String>(this, jsonRequest));
    }

    /**
     * this should NOT run if there are no new records
     * @param results
     */
    public void processResults(String results){
        while(pIterator.hasNext()){
            try {
                createCloudPasswordData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void signOut(){
        startActivity(new Intent(this, MainActivity.class));
    }
}
