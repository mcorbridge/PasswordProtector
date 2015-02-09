package com.mcorbridge.passwordprotector.validators;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.mcorbridge.passwordprotector.R;

/**
 * Created by Mike on 2/9/2015.
 * copyright Michael D. Corbridge
 */
public class CreateValidation {

    private static Context context;
    private static String category;
    private static String title;
    private static String value;
    private static String errorMsg;

    public static boolean validate(Context cntxt, String ctgry, String ttl, String vl){
        errorMsg = "";
        context = cntxt;
        category = ctgry;
        title = ttl;
        value = vl;
        String msg = "";
        if(categoryValidation()){
            msg += categoryError();
        }
        if(titleValidation()){
            msg += titleError();
        }
        if(valueValidation()){
            msg += valueError();
        }
        if(msg.length() != 0){
            errorMsg = msg;
            errorDialog();
            return false;
        }else{
            return true;
        }
    }

    public static boolean validate(Context cntxt, String ttl, String vl){
        errorMsg = "";
        context = cntxt;
        title = ttl;
        value = vl;
        String msg = "";
        if(titleValidation()){
            msg += titleError();
        }
        if(valueValidation()){
            msg += valueError();
        }
        if(msg.length() != 0){
            errorMsg = msg;
            errorDialog();
            return false;
        }else{
            return true;
        }
    }

    public static boolean categoryValidation(){
        return  (category.length() == 0);
    }

    public static boolean titleValidation(){
        return  (title.length() == 0);
    }

    public static boolean valueValidation(){
        return  (value.length() == 0);
    }

    private static String categoryError(){
        return "The category cannot be left empty\n";
    }

    private static String titleError(){
        return "The title cannot be left empty\n";
    }

    private static String valueError(){
        return "The value cannot be left empty";
    }

    public static void errorDialog(){
         new AlertDialog.Builder(context)
                .setTitle("Alert")
                .setMessage("The following errors were found:\n\n" + errorMsg)
                .setIcon(R.drawable.alert_icon)
                .setPositiveButton("Ok, got it", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //startActivity(intent);
                    }
                }).show();
    }

}
