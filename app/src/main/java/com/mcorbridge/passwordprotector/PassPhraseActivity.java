package com.mcorbridge.passwordprotector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.mcorbridge.passwordprotector.practice.PracticeActivity;
import com.mcorbridge.passwordprotector.video.VideoActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PassPhraseActivity extends BaseActivity {

    // the countdownTimer runs at start up to show the PopupWindow
    private CountDownTimer countDownTimer;
    private String question;
    private String answer;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_phrase);

        if(applicationModel.isDevMode()){
            EditText editTextQ = (EditText)findViewById(R.id.editTextQuestion);
            editTextQ.setText("***");
            EditText editTextA = (EditText)findViewById(R.id.editTextAnswer);
            editTextA.setText("***");
            EditText email = (EditText)findViewById(R.id.editTextEmail);
            email.setText("***");
        }
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

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        finish();
    }


    public void doSubmit(View v) throws Exception{
        EditText editTextQ = (EditText)findViewById(R.id.editTextQuestion);
        question = editTextQ.getText().toString().toLowerCase().replaceAll("\\s", "");
        EditText editTextA = (EditText)findViewById(R.id.editTextAnswer);
        answer = editTextA.getText().toString().toLowerCase().replaceAll("\\s", "");
        EditText editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        email = editTextEmail.getText().toString();

        if(!doValidation()){
            return;
        }

        // email
        applicationModel.setEmail(email);

        // question and answer
        String secretQuestionAnswer = question.concat(answer);
        applicationModel.setSecretKey(secretQuestionAnswer);

        System.out.println("secretKey ---> " + applicationModel.getSecretKey());

        Intent intent;

        if(applicationModel.isDevMode()){
            intent = new Intent(this, PracticeActivity.class);
        }else{
            intent = new Intent(this, VideoActivity.class);
        }

        startActivity(intent);
    }

    private boolean doValidation(){
        int ndxValid = 0;
        String errorMsg = "";
        if(question.length() < 1 || answer.length() < 1){
            ndxValid++;
        }

        if(!isValidEmail(email)){
            ndxValid = ndxValid + 2;
        }

        if(ndxValid == 1){
            errorMsg += "You must provide a question and answer.";
        }else if(ndxValid == 2){
            errorMsg += "There is a problem with the email format.";
        }else if(ndxValid == 3){
            errorMsg += "You must provide a question and answer.\n&\nThere is a problem with the email format.";
        }

        if(ndxValid != 0){
            new AlertDialog.Builder(this)
                    .setTitle("Alert")
                    .setMessage(errorMsg)
                    .setIcon(R.drawable.alert_icon)
                    .setPositiveButton("Ok, got it.", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
// stub
                        }
                    })
                    .show();
        }
        return (ndxValid == 0)?true:false;
    }

    private void showPopup(final Activity context) {

        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        int popupHeight = displayMetrics.heightPixels;
        int popupWidth = displayMetrics.widthPixels;

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popout_layout, viewGroup);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight-170);
        popup.setFocusable(true);

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout,Gravity.NO_GRAVITY,0,170);

        // Getting a reference to Close button, and close the popup when clicked.
        Button close = (Button) layout.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
    }

    // timer allows activity view to be rendered before popup window is created and displayed
    public CountDownTimer getCountDownTimer = new CountDownTimer(600,300) {
        @Override
        public void onTick(long millisUntilFinished) {}
        @Override
        public void onFinish() {
            showPopup(PassPhraseActivity.this);
        }
    }.start();

    // email validation
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
