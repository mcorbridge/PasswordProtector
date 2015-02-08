package com.mcorbridge.passwordprotector.read;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mcorbridge.passwordprotector.BaseActivity;
import com.mcorbridge.passwordprotector.JSON.JsonTask;
import com.mcorbridge.passwordprotector.MainActivity;
import com.mcorbridge.passwordprotector.R;
import com.mcorbridge.passwordprotector.adapters.CustomAdapter;
import com.mcorbridge.passwordprotector.constants.ApplicationConstants;
import com.mcorbridge.passwordprotector.create.CreateActivity;
import com.mcorbridge.passwordprotector.encryption.AESEncryption;
import com.mcorbridge.passwordprotector.error.DecipherErrorActivity;
import com.mcorbridge.passwordprotector.interfaces.IPasswordActivity;
import com.mcorbridge.passwordprotector.service.ServletPostAsyncTask;
import com.mcorbridge.passwordprotector.sql.Password;
import com.mcorbridge.passwordprotector.sql.PasswordsDataSource;
import com.mcorbridge.passwordprotector.update.UpdateActivity;
import com.mcorbridge.passwordprotector.vo.PasswordDataVO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReadActivity extends BaseActivity implements IPasswordActivity{

    private ProgressBar progressBar;
    private PasswordsDataSource passwordsDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        // automagically signs the user out after 5 min
        timeOut.setContext(this);

        // for offline data work
        passwordsDataSource = new PasswordsDataSource(getApplicationContext());
        progressBar = (ProgressBar)findViewById(R.id.loadSpinner);
        progressBar.setVisibility(View.VISIBLE);

        try {
            doRead();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!applicationModel.getIsDataConnected()){
            showNotConnectedDialog();
        }

        if(!applicationModel.isTimeoutAware()){
            setApplicationTimeout();
            applicationModel.setTimeoutAware(true);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.getItem(ApplicationConstants.MENU_ITEM_TEST).setVisible(false);
        menu.getItem(ApplicationConstants.MENU_ITEM_HOME).setVisible(true);
        menu.getItem(ApplicationConstants.MENU_ITEM_CREATE).setVisible(true);
        menu.getItem(ApplicationConstants.MENU_ITEM_READ).setVisible(false);
        menu.getItem(ApplicationConstants.MENU_ITEM_VIDEO).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }

    private void doRead() throws Exception{
        String jsonRequest = JsonTask.readJSON();

        //if the read operation has already been performed, use the arrayList in memory
        if(applicationModel.getDecipheredPasswordDataVOs() != null){
            System.out.println("****************** read data from object in memory ********************");
            progressBar.setVisibility(View.INVISIBLE);
            bindPasswordDataToList(applicationModel.getDecipheredPasswordDataVOs());
            return;
        }

        if(applicationModel.getIsDataConnected()){
            postToServlet(jsonRequest);
        }else{
            readFromLocalDatabase();
        }
    }

    /**
     * this method calls the servlet specified in the ServletPostAsyncTask class and onResult calls the
     * processResults method in this class
     * This class, and any class that needs to be part of the request/response cycle MUST implement IPasswordActivity!
     * @param jsonRequest
     * @throws Exception
     */
    private void postToServlet(String jsonRequest) throws  Exception{
        new ServletPostAsyncTask().execute(new Pair<Context, String>(this, jsonRequest));
    }

    private void readFromLocalDatabase(){
        progressBar.setVisibility(View.INVISIBLE);
        passwordsDataSource.open();
        List<Password> passwords = passwordsDataSource.getAllPasswords();
        passwordsDataSource.close();
        PasswordDataVO[] passwordDataVOs = arrayFromQuery(passwords);
        ArrayList<PasswordDataVO> decipheredPasswordDataVOs = doDecipherResult(passwordDataVOs);
        //testDecipheredPasswordDataVOs(decipheredPasswordDataVOs);
        bindPasswordDataToList(decipheredPasswordDataVOs);
    }

    private PasswordDataVO[] arrayFromQuery(List<Password> passwords){
        PasswordDataVO[] passwordDataVOs = new PasswordDataVO[passwords.size()];
        Iterator iterator = passwords.iterator();
        int n = 0;
        while(iterator.hasNext()){
            PasswordDataVO passwordDataVO = new PasswordDataVO();
            Password password = (Password)iterator.next();
            passwordDataVO.setId(password.getPswdID());
            passwordDataVO.setCategory(password.getCategory());
            passwordDataVO.setName(password.getName());
            passwordDataVO.setTitle(password.getTitle());
            passwordDataVO.setAction(password.getAction());
            passwordDataVO.setValue(password.getValue());
            passwordDataVOs[n] = passwordDataVO;
            n++;
        }
        return passwordDataVOs;
    }

    /**
     * called upon result received from servlet
     * @param results
     */
    public void processResults(String results){
        System.out.println("processResults --> " + results);
        progressBar.setVisibility(View.INVISIBLE);
        Gson gson = new Gson();
        final PasswordDataVO[] passwordDataVOs = gson.fromJson(results, PasswordDataVO[].class);

        // take user directly to create if they are new and have not created any records
        if(passwordDataVOs.length == 0){
            startActivity(new Intent(this, CreateActivity.class));
            applicationModel.setNewUser(true);
            return;
        }

        //rebuild the local database IF it needs to be restored
        // and do it inside a new thread
        if(applicationModel.isRequestLocalDatabaseRebuild()){
            new Thread(new Runnable() {
                public void run() {
                    System.out.println("**************************** copying all cloud data into local database ****************************");
                    System.out.println("                                   set rebuild flag to FALSE                                        ");
                    applicationModel.setRequestLocalDatabaseRebuild(false);
                    rebuildLocalDatabase(passwordDataVOs);
                }
            }).start();
        }

        // decrypt the password data
        ArrayList<PasswordDataVO> decipheredPasswordDataVOs = doDecipherResult(passwordDataVOs);

        // we don't want to make unnecessary calls to the server, so place the results in memory
        applicationModel.setDecipheredPasswordDataVOs(decipheredPasswordDataVOs);

        bindPasswordDataToList(applicationModel.getDecipheredPasswordDataVOs());

        //testDecipheredPasswordDataVOs(decipheredPasswordDataVOs);
    }

    private void bindPasswordDataToList(ArrayList<PasswordDataVO> decipheredPasswordDataVOs){
        //bind results to listView
        ArrayAdapter<PasswordDataVO> itemsAdapter = new ArrayAdapter<PasswordDataVO>(this, android.R.layout.simple_list_item_1, decipheredPasswordDataVOs);
        ListView listView = (ListView) findViewById(R.id.passwordList);
        listView.setAdapter(itemsAdapter);
        final CustomAdapter adapter = new CustomAdapter(this, decipheredPasswordDataVOs);
        listView.setAdapter(adapter);
        final Intent intent = new Intent(this, UpdateActivity.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                CustomAdapter customAdapter = (CustomAdapter)parent.getAdapter();
                PasswordDataVO passwordDataVO = customAdapter.getItem(position);
                intent.putExtra("passwordDataVO",passwordDataVO);
                startActivity(intent);
            }
        });

        //because the data was deciphered correctly, the visual key attempts is set back to 0
        //applicationModel.setIncorrectDecipherAttempts(0);
    }

    /**
     * If all records in the local (offline) database have been deleted,
     * this method will rebuild the local database from the most up-to-date records from the cloud
     * @param passwordDataVOs
     */
    private void rebuildLocalDatabase(PasswordDataVO[] passwordDataVOs){
        passwordsDataSource.open();
        for (int n=0;n<passwordDataVOs.length;n++){
            PasswordDataVO passwordDataVO = passwordDataVOs[n];
            String action = ApplicationConstants.CREATE;
            Long id = passwordDataVO.getId();
            String category = passwordDataVO.getCategory();
            String title = passwordDataVO.getTitle();
            String value = passwordDataVO.getValue();
            int modified = 0; // modified = 0 indicates that none of these records will be synchronized in the future
            String name = passwordDataVO.getName();
            passwordsDataSource.createPassword(id,action,category,modified,name,title,value);
        }
        passwordsDataSource.close();
    }

    private ArrayList<PasswordDataVO> doDecipherResult(PasswordDataVO[] passwordDataVOs){
        ArrayList<PasswordDataVO> decipheredPasswordDataVOs = new ArrayList<>();
        try {
            for(int n=0; n<passwordDataVOs.length; n++){
                PasswordDataVO passwordDataVO = passwordDataVOs[n];
                passwordDataVO.setCategory(AESEncryption.decipher(applicationModel.getCipher(),passwordDataVO.getCategory()));
                passwordDataVO.setTitle(AESEncryption.decipher(applicationModel.getCipher(), passwordDataVO.getTitle()));
                passwordDataVO.setValue(AESEncryption.decipher(applicationModel.getCipher(), passwordDataVO.getValue()));

                decipheredPasswordDataVOs.add(passwordDataVO);
            }
        } catch (Exception e) {
            // the decipher will fail if the pass phrase question/answer OR visual key are INCORRECT!
            System.out.println("decipher error");
            startActivity(new Intent(this, DecipherErrorActivity.class));
        }
        return decipheredPasswordDataVOs;
    }

    private void testDecipheredPasswordDataVOs(List<PasswordDataVO> list){
        String newLine = System.getProperty("line.separator");
        Iterator<PasswordDataVO> iterator = list.iterator();
        while(iterator.hasNext()){
            PasswordDataVO passwordDataVO = iterator.next();
            System.out.println(
                    "(id) " + passwordDataVO.getId() + newLine +
                    "(name) " + passwordDataVO.getName() + newLine +
                    "(category) " + passwordDataVO.getCategory() + newLine +
                    "(title) " + passwordDataVO.getTitle() + newLine +
                    "(value) " + passwordDataVO.getValue()
            );
        }
    }

    private void showNotConnectedDialog(){
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
    }

    private void setApplicationTimeout(){
        Toast.makeText(getApplicationContext(), "The application will timeout in 5 minutes",
                Toast.LENGTH_LONG).show();

        timeOut.startTimer();
    }

    public void signOut(){
        startActivity(new Intent(this, MainActivity.class));
    }

    public void showTimeoutWarning(){
        Toast.makeText(getApplicationContext(), "The application will timeout in 1 minute",
                Toast.LENGTH_LONG).show();
    }
}
