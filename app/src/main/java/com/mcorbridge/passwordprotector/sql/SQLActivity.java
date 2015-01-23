package com.mcorbridge.passwordprotector.sql;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mcorbridge.passwordprotector.R;
import com.mcorbridge.passwordprotector.encryption.AESEncryption;
import com.mcorbridge.passwordprotector.model.ApplicationModel;

import java.util.List;

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sql, menu);
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

    public void doCreate(View v)throws Exception{
        passwordsDataSource.open();
        String cipher = applicationModel.getCipher();
        String action = AESEncryption.cipher(cipher,"test");
        String category = AESEncryption.cipher(cipher,"personal");
        boolean modified = true;
        String title = AESEncryption.cipher(cipher,"test");
        String value = AESEncryption.cipher(cipher,"test");
        String name = applicationModel.getEmail();
        passwordsDataSource.createPassword(action,category,modified,name,title,value);
        passwordsDataSource.close();
    }

    public void doRead(View v){
        passwordsDataSource.open();
        List<Password> passwords = passwordsDataSource.getAllPasswords();
        passwordsDataSource.close();
    }

}
