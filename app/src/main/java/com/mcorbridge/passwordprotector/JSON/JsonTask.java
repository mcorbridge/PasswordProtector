package com.mcorbridge.passwordprotector.JSON;

import com.mcorbridge.passwordprotector.constants.ApplicationConstants;
import com.mcorbridge.passwordprotector.encryption.AESUtil;
import com.mcorbridge.passwordprotector.model.ApplicationModel;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by Mike on 1/21/2015.
 * copyright Michael D. Corbridge
 * create json strings to send to servlet
 */
public class JsonTask {

    private static ApplicationModel applicationModel = ApplicationModel.getInstance();

    private static AESUtil aesUtil = new AESUtil();

    private static long id;

    public static long getID(){
        return  id;
    }


    public static String createJSON(String category, String title, String value) throws Exception{
        id = createRndID();

        String cipher = applicationModel.getCipher();

        JSONObject json = new JSONObject();
        json.put("value", aesUtil.encrypt(cipher,value));
        json.put("name", applicationModel.getEmail());
        json.put("category", aesUtil.encrypt(cipher,category));
        json.put("title", aesUtil.encrypt(cipher,title));
        json.put("action", "create");
        json.put("id", id);
        String[] jsonArray = {json.toString()};
        return Arrays.toString(jsonArray);
    }

    public static String updateJSON(String category, String title, String value, Long id) throws Exception{

        String cipher = applicationModel.getCipher();

        JSONObject json = new JSONObject();
        json.put("value", aesUtil.encrypt(cipher,value));
        json.put("name", applicationModel.getEmail());
        json.put("category", aesUtil.encrypt(cipher,category));
        json.put("title", aesUtil.encrypt(cipher,title));
        json.put("action", "update");
        json.put("id", id);
        String[] jsonArray = {json.toString()};
        return Arrays.toString(jsonArray);
    }

    public static String deleteJSON(String category, String title, String value, Long id) throws Exception{

        String cipher = applicationModel.getCipher();

        JSONObject json = new JSONObject();
        json.put("value", aesUtil.encrypt(cipher,value));
        json.put("name", applicationModel.getEmail());
        json.put("category", aesUtil.encrypt(cipher,category));
        json.put("title", aesUtil.encrypt(cipher,title));
        json.put("action", "delete");
        json.put("id", id);
        String[] jsonArray = {json.toString()};
        return Arrays.toString(jsonArray);
    }

    /**
     * this is used exclusively for data that was created and stored in the offline mode
     * @param category
     * @param title
     * @param value
     * @return
     * @throws Exception
     */
    public static String createPreEncryptedJSON(String category, String title, String value, String action, long pswdID) throws Exception{
        JSONObject json = new JSONObject();
        json.put("value", value);
        json.put("name", applicationModel.getEmail());
        json.put("category", category);
        json.put("title",title);
        json.put("action", action);
        if(pswdID > 0L){
            System.out.println("password ID is present - this is an UPDATE (id = " +  pswdID + ")");
            json.put("id", pswdID);
        }else{
            System.out.println("NO password ID is present - this is a CREATE");
            json.put("id", createRndID());
        }

        String[] jsonArray = {json.toString()};
        return Arrays.toString(jsonArray);
    }

    public static String createDeleteAllJSON()throws Exception{
        JSONObject json = new JSONObject();
        json.put("name", applicationModel.getEmail());
        json.put("action", ApplicationConstants.DELETE_ALL);
        String[] jsonArray = {json.toString()};
        return Arrays.toString(jsonArray);
    }

    /**
     *
     * @throws Exception
     */
    public static String readJSON() throws Exception{
        JSONObject json = new JSONObject();
        json.put("name", applicationModel.getEmail());
        json.put("action", "read");
        String[] jsonArray = {json.toString()};
        return Arrays.toString(jsonArray);
    }


    public static long createRndID(){
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }

}
