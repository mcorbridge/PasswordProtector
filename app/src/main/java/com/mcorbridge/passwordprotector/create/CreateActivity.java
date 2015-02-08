package com.mcorbridge.passwordprotector.create;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mcorbridge.passwordprotector.BaseActivity;
import com.mcorbridge.passwordprotector.JSON.JsonTask;
import com.mcorbridge.passwordprotector.MainActivity;
import com.mcorbridge.passwordprotector.PasswordDataActivity;
import com.mcorbridge.passwordprotector.R;
import com.mcorbridge.passwordprotector.constants.ApplicationConstants;
import com.mcorbridge.passwordprotector.encryption.AESEncryption;
import com.mcorbridge.passwordprotector.interfaces.IPasswordActivity;
import com.mcorbridge.passwordprotector.read.ReadActivity;
import com.mcorbridge.passwordprotector.service.ServletPostAsyncTask;
import com.mcorbridge.passwordprotector.sql.PasswordsDataSource;
import com.mcorbridge.passwordprotector.vo.PasswordDataVO;

import java.util.ArrayList;

public class CreateActivity extends BaseActivity implements IPasswordActivity{

    private PasswordsDataSource passwordsDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        // automagically signs the user out after 5 min
        timeOut.setContext(this);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        // for offline data work
        passwordsDataSource = new PasswordsDataSource(getApplicationContext());

        if(applicationModel.isNewUser()){
            new AlertDialog.Builder(this)
                    .setTitle("Alert")
                    .setMessage(R.string.empty_database)
                    .setIcon(R.drawable.alert_icon)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
// stub
                        }
                    })
                    .show();

            applicationModel.setNewUser(false);
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
        menu.getItem(ApplicationConstants.MENU_ITEM_CREATE).setVisible(false);
        menu.getItem(ApplicationConstants.MENU_ITEM_READ).setVisible(true);
        menu.getItem(ApplicationConstants.MENU_ITEM_VIDEO).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(this, ReadActivity.class));
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }

    public void doSubmit(View v) throws Exception{
        EditText editTextCategory = (EditText)findViewById(R.id.editTextCategory);
        EditText editTextTitle = (EditText)findViewById(R.id.editTextTitle);
        EditText editTextValue = (EditText)findViewById(R.id.editTextValue);

        String category = editTextCategory.getText().toString();
        String title = editTextTitle.getText().toString();
        String value = editTextValue.getText().toString();

        //'personal' remains the default category
        if(category.length() == 0){
            category = "personal";
        }

        String jsonRequest = JsonTask.createJSON(category,title,value);

        // If the app is has a network connection save to cloud and save local db simultaneously!
        if(applicationModel.getIsDataConnected()){
            postToServlet(jsonRequest); //
            saveToLocalDatabase(JsonTask.getID(),category,title,value,0); // '0' flags this as a value that does NOT need to be synchronized with the cloud
        }else{
            saveToLocalDatabase(JsonTask.getID(), category, title, value, 1); // '1' flags this as a value that MUST be synchronized with the cloud
        }

        // here we add a new password object to the password objects in memory
        // this is NOT persisted, and is only done to speed up the app
        // I do this because I intend to add a feature that gives the user the option to NOT store data locally (SQLite) - but we still need the speed
        if(applicationModel.getDecipheredPasswordDataVOs() != null){
            ArrayList<PasswordDataVO> passwordDataVOs = applicationModel.getDecipheredPasswordDataVOs();
            PasswordDataVO passwordDataVO = new PasswordDataVO();
            passwordDataVO.setAction(ApplicationConstants.CREATE);
            passwordDataVO.setId(JsonTask.getID());
            passwordDataVO.setCategory(category);
            passwordDataVO.setTitle(title);
            passwordDataVO.setValue(value);
            passwordDataVOs.add(passwordDataVO);
        }

        Intent intent = new Intent(this, PasswordDataActivity.class);
        startActivity(intent);
    }

    private void saveToLocalDatabase(Long id, String category, String title, String value, int modified)throws Exception{
        passwordsDataSource.open();
        String cipher = applicationModel.getCipher();
        String action = ApplicationConstants.CREATE; // the action is not encrypted
        category = AESEncryption.cipher(cipher,category);
        title = AESEncryption.cipher(cipher,title);
        value = AESEncryption.cipher(cipher,value);
        String name = applicationModel.getEmail();
        int isModified = modified; // I changed this from boolean to int. Why? I made a booboo  - but decided to stick with int in the end
        passwordsDataSource.createPassword(id,action,category,isModified,name,title,value);
        passwordsDataSource.close();
    }

    private void postToServlet(String jsonRequest) throws  Exception{
        new ServletPostAsyncTask().execute(new Pair<Context, String>(this, jsonRequest));
    }

    public void processResults(String results){
        System.out.println(results);
    }

    private void setApplicationTimeout(){
        Toast.makeText(getApplicationContext(), "The application will timeout in " + ApplicationConstants.SECONDS_PER_ONE_MIN + " minute(s)",
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
