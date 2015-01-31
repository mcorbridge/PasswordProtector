package com.mcorbridge.passwordprotector.read;

import android.app.Activity;
import android.content.Context;
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

import com.google.gson.Gson;
import com.mcorbridge.passwordprotector.JSON.JsonTask;
import com.mcorbridge.passwordprotector.R;
import com.mcorbridge.passwordprotector.adapters.CustomAdapter;
import com.mcorbridge.passwordprotector.encryption.AESEncryption;
import com.mcorbridge.passwordprotector.interfaces.IPasswordActivity;
import com.mcorbridge.passwordprotector.model.ApplicationModel;
import com.mcorbridge.passwordprotector.service.ServletPostAsyncTask;
import com.mcorbridge.passwordprotector.sql.Password;
import com.mcorbridge.passwordprotector.sql.PasswordsDataSource;
import com.mcorbridge.passwordprotector.update.UpdateActivity;
import com.mcorbridge.passwordprotector.vo.PasswordDataVO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReadActivity extends Activity implements IPasswordActivity{

    private ApplicationModel applicationModel;
    private ProgressBar progressBar;
    private PasswordsDataSource passwordsDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        applicationModel = ApplicationModel.getInstance();

        // for offline data work
        passwordsDataSource = new PasswordsDataSource(getApplicationContext());
        progressBar = (ProgressBar)findViewById(R.id.loadSpinner);
        progressBar.setVisibility(View.VISIBLE);

        try {
            doRead();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_read, menu);
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

    private void doRead() throws Exception{
        String jsonRequest = JsonTask.readJSON();
        if(applicationModel.getIsDataConnected()){
            postToServlet(jsonRequest);
        }else{
            readFromLocalDatabase();
        }
    }

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

    public void processResults(String results){
        System.out.println("--> " + results);
        progressBar.setVisibility(View.INVISIBLE);
        Gson gson = new Gson();
        final PasswordDataVO[] passwordDataVOs = gson.fromJson(results, PasswordDataVO[].class);

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
        bindPasswordDataToList(decipheredPasswordDataVOs);

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
    }

    /**
     * At least one passwordData was created offline, and this value has been uploaded to cloud storage.
     * As a result of this, all records in the local (offline) database have been deleted.
     * This method will rebuild the local database from the most up-to-date records from the cloud
     * @param passwordDataVOs
     */
    private void rebuildLocalDatabase(PasswordDataVO[] passwordDataVOs){
        passwordsDataSource.open();
        for (int n=0;n<passwordDataVOs.length;n++){
            PasswordDataVO passwordDataVO = passwordDataVOs[n];
            String action = "create";
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
            System.out.println("decipher error");
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
}
