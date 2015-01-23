package com.mcorbridge.passwordprotector.sql;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mcorbridge.passwordprotector.R;
import com.mcorbridge.passwordprotector.model.ApplicationModel;

import java.util.Date;
import java.util.List;

public class SQLActivity extends Activity {

    ApplicationModel applicationModel;
    CommentsDataSource commentsDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sql);

        applicationModel = ApplicationModel.getInstance();

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        commentsDataSource = new CommentsDataSource(getApplicationContext());
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

    public void doCreate(View v){
        commentsDataSource.open();
        commentsDataSource.createComment("foo " + getDate());
        commentsDataSource.close();
    }

    public void doRead(View v){
        commentsDataSource.open();
        List<Comment> comments = commentsDataSource.getAllComments();
        commentsDataSource.close();
    }

    private String getDate(){
        Date date = new Date();
        return date.toString();
    }
}
