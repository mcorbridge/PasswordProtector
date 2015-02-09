package com.mcorbridge.passwordprotector.validators;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.mcorbridge.passwordprotector.R;

/**
 * Created by Mike on 2/9/2015.
 * copyright Michael D. Corbridge
 *
 * String validation removes all whitespaces and non visible characters such as tab
 */
public class CreateValidation {

    private static Context context;
    private static String category;
    private static String title;
    private static String value;

    /**
     *
     * @param cntxt
     * @param ctgry
     * @param ttl
     * @param vl
     * @return
     */
    public static boolean validate(Context cntxt, String ctgry, String ttl, String vl){
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
            errorDialog(msg);
            return false;
        }else{
            return true;
        }
    }

    /**
     *
     * @param cntxt
     * @param ttl
     * @param vl
     * @return
     */
    public static boolean validate(Context cntxt, String ttl, String vl){
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
            errorDialog(msg);
            return false;
        }else{
            return true;
        }
    }

    /**
     *
     * @return
     */
    public static boolean categoryValidation(){
        return  (category.replaceAll("\\s+","").length() == 0);
    }

    /**
     *
     * @return
     */
    public static boolean titleValidation(){
        return  (title.replaceAll("\\s+","").length() == 0);
    }

    /**
     *
     * @return
     */
    public static boolean valueValidation(){
        return  (value.replaceAll("\\s+","").length() == 0);
    }

    /**
     *
     * @return
     */
    private static String categoryError(){
        return "The Category cannot be left empty\n";
    }

    /**
     *
     * @return
     */
    private static String titleError(){
        return "The Title cannot be left empty\n";
    }

    /**
     *
     * @return
     */
    private static String valueError(){
        return "The Value cannot be left empty";
    }

    /**
     *
     */
    public static void errorDialog(String errorMsg){
         new AlertDialog.Builder(context)
                .setTitle("Alert")
                .setMessage("The following errors were found:\n\n" + errorMsg)
                .setIcon(R.drawable.alert_icon)
                .setPositiveButton("Ok, got it", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //stub
                    }
                }).show();
    }

}
