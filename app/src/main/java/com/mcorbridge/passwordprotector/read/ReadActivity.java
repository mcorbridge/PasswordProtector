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
        progressBar.setVisibility(View.INVISIBLE);
        Gson gson = new Gson();
        PasswordDataVO[] passwordDataVOs = gson.fromJson(results, PasswordDataVO[].class);
        // decrypt the password data
        ArrayList<PasswordDataVO> decipheredPasswordDataVOs = doDecipherResult(passwordDataVOs);
        bindPasswordDataToList(decipheredPasswordDataVOs);
        findCloudModifiedPasswordVOs(decipheredPasswordDataVOs);
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

    private void findCloudModifiedPasswordVOs(ArrayList<PasswordDataVO> decipheredPasswordDataVOs){
        Iterator iterator = decipheredPasswordDataVOs.iterator();
        while(iterator.hasNext()){
            PasswordDataVO passwordDataVO = (PasswordDataVO)iterator.next();
            if(passwordDataVO.getAction().equals("modified") || passwordDataVO.getAction().equals("delete")){
                /* todo the local (offline) version of the data must be updated
                   additionally, the online property 'modified' or 'deleted' must be changed so that offline data is not updated again
                   thoughts... change the online version back to 'created' and 'deleted' to 'deleted_x' */
            }
        }
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
