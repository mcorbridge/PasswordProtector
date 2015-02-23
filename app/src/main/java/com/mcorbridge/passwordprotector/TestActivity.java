package com.mcorbridge.passwordprotector;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mcorbridge.passwordprotector.JSON.JsonTask;
import com.mcorbridge.passwordprotector.encryption.AESUtil;
import com.mcorbridge.passwordprotector.sql.Password;
import com.mcorbridge.passwordprotector.sql.PasswordsDataSource;

import java.util.List;


public class TestActivity extends BaseActivity {

    private PasswordsDataSource passwordsDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // for offline data work
        passwordsDataSource = new PasswordsDataSource(getApplicationContext());
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

    public void readFromLocalDatabase(View v){
        passwordsDataSource.open();
        List<Password> passwords = passwordsDataSource.getAllPasswords(true);
        passwordsDataSource.close();
    }

    // set modified to 1 indicating that this value needs to be synchronized with the cloud
    public void addToLocalDatabase1(View v) throws Exception{
            passwordsDataSource.open();
            String action = "create";
            String category = "category";
            String title = "title";
            String value = "value";
            String name = "mikecorbridge@gmail.com";
            passwordsDataSource.createPassword(JsonTask.createRndID(),action,category,1,name,title,value);
            passwordsDataSource.close();
    }

    // set modified to 0 indicating that this value does NOT need to be synchronized with the cloud
    public void addToLocalDatabase0(View v) throws Exception{
        passwordsDataSource.open();
        String action = "create";
        String category = "category";
        String title = "title";
        String value = "value";
        String name = "mikecorbridge@gmail.com";
        passwordsDataSource.createPassword(JsonTask.createRndID(),action,category,0,name,title,value);
        passwordsDataSource.close();
    }

    public void clearDatabase(View v){
        System.out.println("**************************** delete all values in local database ****************************");
        passwordsDataSource.open();
        passwordsDataSource.deleteLocalPasswordData();
        passwordsDataSource.close();
    }

    public void clearLockOut(View v){
        System.out.println("**************************** set lockout time to null ****************************");
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("lockout_time", null);  // Saving string
        editor.apply(); // commit changes
        applicationModel.setIncorrectDecipherAttempts(0);
    }

    public void testAES(View v){
        System.out.println("**************************** test new AES algorithm ****************************");
        AESUtil aesUtil = new AESUtil();
        String cipherText = aesUtil.encrypt("passphrase","test text");
        System.out.println("cipherText ---> " + cipherText);
        String plaintext = aesUtil.decrypt("passphrase",cipherText);
        System.out.println("plaintext ---> " + plaintext);
    }

}
