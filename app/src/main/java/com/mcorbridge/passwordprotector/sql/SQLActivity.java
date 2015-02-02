package com.mcorbridge.passwordprotector.sql;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mcorbridge.passwordprotector.JSON.JsonTask;
import com.mcorbridge.passwordprotector.R;
import com.mcorbridge.passwordprotector.encryption.AESEncryption;
import com.mcorbridge.passwordprotector.model.ApplicationModel;

import java.util.List;

/**
 * this is a test class and is not used in the final application
 */
public class SQLActivity extends Activity {

    ApplicationModel applicationModel;
    PasswordsDataSource passwordsDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sql);

        applicationModel = ApplicationModel.getInstance();

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

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

    public void doCreate(View v)throws Exception{
        passwordsDataSource.open();
        String cipher = applicationModel.getCipher();
        String action = AESEncryption.cipher(cipher,"test");
        String category = AESEncryption.cipher(cipher,"personal");
        int modified = 1;
        String title = AESEncryption.cipher(cipher,"test");
        String value = AESEncryption.cipher(cipher,"test");
        String name = applicationModel.getEmail();
        passwordsDataSource.createPassword(JsonTask.createRndID(),action,category,modified,name,title,value);
        passwordsDataSource.close();
    }

    public void doRead(View v){
        passwordsDataSource.open();
        List<Password> passwords = passwordsDataSource.getAllPasswords();
        passwordsDataSource.close();
    }

}
