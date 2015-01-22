package com.mcorbridge.passwordprotector.read;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.mcorbridge.passwordprotector.JSON.JsonTask;
import com.mcorbridge.passwordprotector.R;
import com.mcorbridge.passwordprotector.encryption.AESEncryption;
import com.mcorbridge.passwordprotector.interfaces.IPasswordActivity;
import com.mcorbridge.passwordprotector.model.ApplicationModel;
import com.mcorbridge.passwordprotector.service.ServletPostAsyncTask;
import com.mcorbridge.passwordprotector.vo.PasswordDataVO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReadActivity extends Activity implements IPasswordActivity{

    private ApplicationModel applicationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        applicationModel = ApplicationModel.getInstance();

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
        postToServlet(jsonRequest);
    }

    private void postToServlet(String jsonRequest) throws  Exception{
        new ServletPostAsyncTask().execute(new Pair<Context, String>(this, jsonRequest));
    }

    public void processResults(String results){
        Gson gson = new Gson();
        PasswordDataVO[] passwordDataVOs = gson.fromJson(results, PasswordDataVO[].class);

        // decrypt the password data
        List<PasswordDataVO> decipheredPasswordDataVOs = doDecipherResult(passwordDataVOs);

        testDecipheredPasswordDataVOs(decipheredPasswordDataVOs);
    }

    private List<PasswordDataVO> doDecipherResult(PasswordDataVO[] passwordDataVOs){
        List<PasswordDataVO> decipheredPasswordDataVOs = new ArrayList<>();
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
