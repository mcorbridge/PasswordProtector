package com.mcorbridge.passwordprotector.create;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.mcorbridge.passwordprotector.JSON.JsonTask;
import com.mcorbridge.passwordprotector.PasswordDataActivity;
import com.mcorbridge.passwordprotector.R;
import com.mcorbridge.passwordprotector.interfaces.IPasswordActivity;
import com.mcorbridge.passwordprotector.model.ApplicationModel;
import com.mcorbridge.passwordprotector.service.ServletPostAsyncTask;

public class CreateActivity extends Activity implements IPasswordActivity{

    ApplicationModel applicationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        applicationModel = ApplicationModel.getInstance();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create, menu);
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

    public void doSubmit(View v) throws Exception{
        EditText editTextCategory = (EditText)findViewById(R.id.editTextCategory);
        EditText editTextTitle = (EditText)findViewById(R.id.editTextTitle);
        EditText editTextValue = (EditText)findViewById(R.id.editTextValue);

        String category = editTextCategory.getText().toString();
        String title = editTextTitle.getText().toString();
        String value = editTextValue.getText().toString();

        if(category.length() == 0){
            category = "personal";
        }

        String jsonRequest = JsonTask.createJSON(category,title,value);

        if(applicationModel.getIsDataConnected()){
            postToServlet(jsonRequest);
        }else{
            saveToLocal(jsonRequest);
        }

        Intent intent = new Intent(this, PasswordDataActivity.class);
        startActivity(intent);
    }

    private void saveToLocal(String jsonRequest){
        //TODO save to local sqLite db
    }

    private void postToServlet(String jsonRequest) throws  Exception{
        new ServletPostAsyncTask().execute(new Pair<Context, String>(this, jsonRequest));
    }

    public void processResults(String results){
        System.out.println(results);
    }
}
