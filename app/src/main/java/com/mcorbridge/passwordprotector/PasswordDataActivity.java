package com.mcorbridge.passwordprotector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mcorbridge.passwordprotector.JSON.JsonTask;
import com.mcorbridge.passwordprotector.create.CreateActivity;
import com.mcorbridge.passwordprotector.interfaces.IPasswordActivity;
import com.mcorbridge.passwordprotector.model.ApplicationModel;
import com.mcorbridge.passwordprotector.read.ReadActivity;
import com.mcorbridge.passwordprotector.service.ServletPostAsyncTask;
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
    private List<PasswordDataVO> passwordDataVOs;
    Iterator pIterator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_data);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        applicationModel = ApplicationModel.getInstance();

        // for offline data work
        passwordsDataSource = new PasswordsDataSource(getApplicationContext());

        //offline-online mode check for wifi and/or data over telecom
        checkMobileDataConnectivity();

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
    public void checkMobileDataConnectivity(){
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
                passwordDataVO.setCategory(password.getCategory());
                passwordDataVO.setName(password.getName());
                passwordDataVO.setTitle(password.getTitle());
                passwordDataVO.setAction(password.getAction());
                passwordDataVO.setValue(password.getValue());
                passwordDataVOs.add(passwordDataVO);
            }
        }

        // if offline data was added, rebuild the local db with the cloud data
        // when we have received a copy of at as per a read request
        if(passwordDataVOs.size() > 0){
            passwordsDataSource.open();
            passwordsDataSource.deleteLocalPasswordData();
            passwordsDataSource.close();
            applicationModel.setRequestLocalDatabaseRebuild(true); // flag the app to rebuild the local database
            pIterator = passwordDataVOs.iterator();
        }

        // if nothing is in the db, we should synchronize with the cloud version
        if(getNumValuesInLocalDatabase() == 0){
            applicationModel.setRequestLocalDatabaseRebuild(true); // flag the app to rebuild the local database
        }
    }

    public int getNumValuesInLocalDatabase(){
        passwordsDataSource.open();
        List<Password> passwords = passwordsDataSource.getAllPasswords();
        passwordsDataSource.close();
        return passwords.size();
    }


    private void saveToLocalDatabase(Long id,String category, String title, String value)throws Exception{
        passwordsDataSource.open();
        String action = "create";
        String name = applicationModel.getEmail();
        passwordsDataSource.createPassword(id,action,category,0,name,title,value);
        passwordsDataSource.close();
    }

    private void createCloudPasswordData() throws Exception{
        if(pIterator == null)
            return;
        PasswordDataVO passwordDataVO = (PasswordDataVO)pIterator.next();
        // note that the data is already encrypted
        String jsonRequest = JsonTask.createPreEncryptedJSON(passwordDataVO.getCategory(), passwordDataVO.getTitle(), passwordDataVO.getValue());
        System.out.println("******* cloud synchronization *******");
        System.out.println(jsonRequest);
        postToServlet(jsonRequest);
    }

    private void postToServlet(String jsonRequest) throws  Exception{
        new ServletPostAsyncTask().execute(new Pair<Context, String>(this, jsonRequest));
    }

    /**
     * ideally this should NOT run if there are no new records
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
}
