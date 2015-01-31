package com.mcorbridge.passwordprotector.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.TextKeyListener;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.mcorbridge.passwordprotector.JSON.JsonTask;
import com.mcorbridge.passwordprotector.R;
import com.mcorbridge.passwordprotector.constants.ApplicationConstants;
import com.mcorbridge.passwordprotector.encryption.AESEncryption;
import com.mcorbridge.passwordprotector.interfaces.IPasswordActivity;
import com.mcorbridge.passwordprotector.model.ApplicationModel;
import com.mcorbridge.passwordprotector.service.ServletPostAsyncTask;
import com.mcorbridge.passwordprotector.sql.Password;
import com.mcorbridge.passwordprotector.sql.PasswordsDataSource;
import com.mcorbridge.passwordprotector.vo.PasswordDataVO;

import java.util.ArrayList;
import java.util.Iterator;

public class UpdateActivity extends Activity implements IPasswordActivity{

    PasswordDataVO passwordDataVO;
    EditText category;
    EditText title;
    EditText value;
    Button buttonModify;
    Button buttonDelete;
    ApplicationModel applicationModel;
    private PasswordsDataSource passwordsDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        applicationModel = ApplicationModel.getInstance();

        // for offline data work
        passwordsDataSource = new PasswordsDataSource(getApplicationContext());

        savedInstanceState = getIntent().getExtras();
        passwordDataVO = (PasswordDataVO)savedInstanceState.getSerializable("passwordDataVO");

        setEditTextFields();
        category = (EditText)findViewById(R.id.editTextCategory);
        title = (EditText)findViewById(R.id.editTextTitle);
        value = (EditText)findViewById(R.id.editTextValue);
        buttonModify = (Button)findViewById(R.id.buttonModify);
        buttonDelete = (Button)findViewById(R.id.buttonDelete);

        final ToggleButton toggleButton = (ToggleButton)findViewById(R.id.toggleButton);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggleButton.isChecked()){
                    category.setKeyListener(TextKeyListener.getInstance());
                    category.setFocusable(true);
                    category.setFocusableInTouchMode(true);
                    category.setClickable(true);

                    title.setKeyListener(TextKeyListener.getInstance());
                    title.setFocusable(true);
                    title.setFocusableInTouchMode(true);
                    title.setClickable(true);

                    value.setKeyListener(TextKeyListener.getInstance());
                    value.setFocusable(true);
                    value.setFocusableInTouchMode(true);
                    value.setClickable(true);

                    buttonModify.setEnabled(true);
                    buttonDelete.setEnabled(true);
                }else{
                    category.setKeyListener(null);
                    category.setFocusable(false);
                    category.setFocusableInTouchMode(false);
                    category.setClickable(false);

                    title.setKeyListener(null);
                    title.setFocusable(false);
                    title.setFocusableInTouchMode(false);
                    title.setClickable(false);

                    value.setKeyListener(null);
                    value.setFocusable(false);
                    value.setFocusableInTouchMode(false);
                    value.setClickable(false);

                    buttonModify.setEnabled(false);
                    buttonDelete.setEnabled(false);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_update, menu);
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

    private void setEditTextFields(){
        EditText category = (EditText)findViewById(R.id.editTextCategory);
        EditText title = (EditText)findViewById(R.id.editTextTitle);
        EditText value = (EditText)findViewById(R.id.editTextValue);

        category.setText(passwordDataVO.getCategory());
        title.setText(passwordDataVO.getTitle());
        value.setText(passwordDataVO.getValue());
    }

    public void doModify(View v){
        new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage(R.string.info_update)
                .setIcon(R.drawable.alert_icon)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // stub
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            modifyPasswordData();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show();
    }

    public void doDelete(View v){
        new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage(R.string.info_delete)
                .setIcon(R.drawable.alert_icon)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // stub
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            deletePasswordData();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .show();
    }

    private void modifyPasswordData() throws  Exception{
        String cty = category.getText().toString();
        String ttl = title.getText().toString();
        String vlu = value.getText().toString();
        Long id = passwordDataVO.getId();

        if(applicationModel.getIsDataConnected()){
            String jsonRequest = JsonTask.updateJSON(cty, ttl, vlu, id);
            new ServletPostAsyncTask().execute(new Pair<Context, String>(this, jsonRequest));
            // although this a password UPDATE, the action is set to CREATE since both the cloud and local versions are updated simultaneously
            updateValueLocalDatabase(id, cty, ttl, vlu, 0, ApplicationConstants.CREATE);
        }else{
            updateValueLocalDatabase(id, cty, ttl, vlu, 1, ApplicationConstants.UPDATE);
        }

        //here we add a new password object to the password objects in memory
        // this is NOT persisted, and is only done to speed up the app
        // I do this because I intend to add a feature that gives the user the option to NOT store data locally (SQLite) - but we still need the speed
        if(applicationModel.getDecipheredPasswordDataVOs() != null){
            System.out.println("****************** update data from object in memory ********************");
            ArrayList<PasswordDataVO> passwordDataVOs = applicationModel.getDecipheredPasswordDataVOs();
            Iterator<PasswordDataVO> iterator = passwordDataVOs.iterator();
            while(iterator.hasNext()){
                PasswordDataVO passwordDataVO = iterator.next();
                System.out.println(passwordDataVO.getId() + " == " + id);
                if(passwordDataVO.getId().equals(id)){
                    passwordDataVO.setAction(ApplicationConstants.CREATE);
                    passwordDataVO.setCategory(cty);
                    passwordDataVO.setTitle(ttl);
                    passwordDataVO.setValue(vlu);
                    break;
                }
            }
        }

    }

    private void deletePasswordData() throws  Exception{
        String cty = category.getText().toString();
        String ttl = title.getText().toString();
        String vlu = value.getText().toString();
        Long id = passwordDataVO.getId();

        if(applicationModel.getIsDataConnected()){
            String jsonRequest = JsonTask.deleteJSON(cty, ttl, vlu, id);
            new ServletPostAsyncTask().execute(new Pair<Context, String>(this, jsonRequest));
            deleteValueLocalDatabase(id, 0);
        }else{
            deleteValueLocalDatabase(id, 1);
        }

        //here we add a new password object to the password objects in memory
        // this is NOT persisted, and is only done to speed up the app
        // I do this because I intend to add a feature that gives the user the option to NOT store data locally (SQLite) - but we still need the speed
        if(applicationModel.getDecipheredPasswordDataVOs() != null){
            System.out.println("****************** flag delete object in memory ********************");
            ArrayList<PasswordDataVO> passwordDataVOs = applicationModel.getDecipheredPasswordDataVOs();
            Iterator<PasswordDataVO> iterator = passwordDataVOs.iterator();
            while(iterator.hasNext()){
                PasswordDataVO passwordDataVO = iterator.next();
                System.out.println(passwordDataVO.getId() + " == " + id);
                if(passwordDataVO.getId().equals(id)){
                    passwordDataVO.setAction(ApplicationConstants.DELETE);
                    break;
                }
            }
        }

    }

    private void updateValueLocalDatabase(Long id, String category, String title, String value, int modified, String action)throws Exception{
        passwordsDataSource.open();
        Password password = new Password();
        String cipher = applicationModel.getCipher();
        password.setAction(action);
        password.setCategory(AESEncryption.cipher(cipher, category));
        password.setTitle(AESEncryption.cipher(cipher, title));
        password.setValue(AESEncryption.cipher(cipher, value));
        password.setPswdID(id);
        password.setModified(modified);
        passwordsDataSource.updatePassword(password);
        passwordsDataSource.close();
    }

    private void deleteValueLocalDatabase(Long id, int modified)throws Exception{
        passwordsDataSource.open();
        Password password = new Password();
        password.setAction(ApplicationConstants.DELETE);
        password.setPswdID(id);
        password.setModified(modified);
        passwordsDataSource.deletePassword(password);
        passwordsDataSource.close();
    }

    public void processResults(String results){
        System.out.println(results);
    }
}
