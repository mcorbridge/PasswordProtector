package com.mcorbridge.passwordprotector.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.TextKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.mcorbridge.passwordprotector.R;
import com.mcorbridge.passwordprotector.model.ApplicationModel;
import com.mcorbridge.passwordprotector.vo.PasswordDataVO;

public class UpdateActivity extends Activity {

    PasswordDataVO passwordDataVO;
    EditText category;
    EditText title;
    EditText value;
    Button buttonModify;
    Button buttonDelete;
    ApplicationModel applicationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        applicationModel = ApplicationModel.getInstance();

        savedInstanceState = getIntent().getExtras();
        passwordDataVO = (PasswordDataVO)savedInstanceState.getSerializable("passwordDataVO");

        setEditTextFields();
        category = (EditText)findViewById(R.id.editTextCategory);
        title = (EditText)findViewById(R.id.editTextTitle);
        value = (EditText)findViewById(R.id.editTextValue);
        buttonModify = (Button)findViewById(R.id.buttonModify);
        buttonDelete = (Button)findViewById(R.id.buttonDelete);

        final ToggleButton toggleButton = (ToggleButton)findViewById(R.id.toggleButton);

        // the plan at this point is to NOT allow offline modification (update or delete)
        // the reason is that it is complicated to synchronize date between the local and cloud
        // (I just need some more time to think about it)
        if(!applicationModel.getIsDataConnected()){
            toggleButton.setEnabled(false);
        }

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
                .setMessage("You are about to modify password information\nIs this what you want to do?")
                .setIcon(R.drawable.alert_icon)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // stub
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // stub
                    }
                })
                .show();
    }

    public void doDelete(View v){
        new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage("You are about to delete password information\nIs this what you want to do?")
                .setIcon(R.drawable.alert_icon)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // stub
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // stub
                    }
                })
                .show();
    }
}
