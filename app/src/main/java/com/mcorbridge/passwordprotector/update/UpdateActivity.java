package com.mcorbridge.passwordprotector.update;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.TextKeyListener;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mcorbridge.passwordprotector.BaseActivity;
import com.mcorbridge.passwordprotector.JSON.JsonTask;
import com.mcorbridge.passwordprotector.PasswordDataActivity;
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

public class UpdateActivity extends BaseActivity implements IPasswordActivity{

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
        super.onCreateOptionsMenu(menu);
        menu.getItem(ApplicationConstants.MENU_ITEM_TEST).setVisible(true);
        menu.getItem(ApplicationConstants.MENU_ITEM_HOME).setVisible(true);
        menu.getItem(ApplicationConstants.MENU_ITEM_CREATE).setVisible(true);
        menu.getItem(ApplicationConstants.MENU_ITEM_READ).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }

    private void setEditTextFields(){
        EditText category = (EditText)findViewById(R.id.editTextCategory);
        EditText title = (EditText)findViewById(R.id.editTextTitle);
        EditText value = (EditText)findViewById(R.id.editTextValue);

        category.setText(passwordDataVO.getCategory());
        title.setText(passwordDataVO.getTitle());
        value.setText(passwordDataVO.getValue());
    }

    /**
     *
     * @param v
     */
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

    /**
     *
     * @param v
     */
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

    /**
     *
     * @throws Exception
     */
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

        modifyPasswordDataInMemory(id, cty, ttl, vlu, ApplicationConstants.CREATE);

    }

    /**
     *
     * @throws Exception
     */
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

        modifyPasswordDataInMemory(id, null, null, null, ApplicationConstants.DELETE);

    }

    /**
     * here we add a new password object to the password objects in memory
     * this is NOT persisted, and is only done to speed up the app
     * I do this because I intend to add a feature that gives the user the option to NOT store data locally (SQLite) - but we still need the speed
     * @param id
     * @param category
     * @param title
     * @param value
     * @param action
     */
    private void modifyPasswordDataInMemory(Long id, String category, String title, String value, String action){
        ArrayList<PasswordDataVO> passwordDataVOs = applicationModel.getDecipheredPasswordDataVOs();
        Iterator<PasswordDataVO> iterator = passwordDataVOs.iterator();
        if(action == ApplicationConstants.CREATE){
            while(iterator.hasNext()){
                PasswordDataVO passwordDataVO = iterator.next();
                System.out.println(passwordDataVO.getId() + " == " + id);
                if(passwordDataVO.getId().equals(id)){
                    passwordDataVO.setAction(ApplicationConstants.CREATE);
                    passwordDataVO.setCategory(category);
                    passwordDataVO.setTitle(title);
                    passwordDataVO.setValue(value);
                    break;
                }
            }
        }else{
            while(iterator.hasNext()){
                PasswordDataVO passwordDataVO = iterator.next();
                System.out.println(passwordDataVO.getId() + " == " + id);
                if(passwordDataVO.getId().equals(id)){
                    passwordDataVO.setAction(ApplicationConstants.DELETE);
                    break;
                }
            }
        }

        Intent intent = new Intent(this, PasswordDataActivity.class);
        startActivity(intent);
    }

    /**
     *
     * @param id
     * @param category
     * @param title
     * @param value
     * @param modified
     * @param action
     * @throws Exception
     */
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

    /**
     *
     * @param id
     * @param modified
     * @throws Exception
     */
    private void deleteValueLocalDatabase(Long id, int modified)throws Exception{
        passwordsDataSource.open();
        Password password = new Password();
        password.setAction(ApplicationConstants.DELETE);
        password.setPswdID(id);
        password.setModified(modified);
        passwordsDataSource.deletePassword(password);
        passwordsDataSource.close();
    }

    /**
     *
     * @param results
     */
    public void processResults(String results){
        System.out.println(results);
        Toast.makeText(getApplicationContext(), results,
                Toast.LENGTH_LONG).show();
    }
}
