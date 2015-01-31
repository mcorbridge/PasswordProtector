package com.mcorbridge.passwordprotector.JSON;

import com.mcorbridge.passwordprotector.encryption.AESEncryption;
import com.mcorbridge.passwordprotector.model.ApplicationModel;

import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by Mike on 1/21/2015.
 * copyright Michael D. Corbridge
 * create json strings to send to servlet
 */
public class JsonTask {

    private static ApplicationModel applicationModel = ApplicationModel.getInstance();

    private static long id;

    public static long getID(){
        return  id;
    }


    public static String createJSON(String category, String title, String value) throws Exception{
        id = createRndID();
        JSONObject json = new JSONObject();
        json.put("value", AESEncryption.cipher(applicationModel.getCipher(),value));
        json.put("name", applicationModel.getEmail());
        json.put("category", AESEncryption.cipher(applicationModel.getCipher(),category));
        json.put("title", AESEncryption.cipher(applicationModel.getCipher(),title));
        json.put("action", "create");
        json.put("id", id);
        return json.toString();
    }

    public static String updateJSON(String category, String title, String value, Long id) throws Exception{
        JSONObject json = new JSONObject();
        json.put("value", AESEncryption.cipher(applicationModel.getCipher(),value));
        json.put("name", applicationModel.getEmail());
        json.put("category", AESEncryption.cipher(applicationModel.getCipher(),category));
        json.put("title", AESEncryption.cipher(applicationModel.getCipher(),title));
        json.put("action", "update");
        json.put("id", id);
        return json.toString();
    }

    public static String deleteJSON(String category, String title, String value, Long id) throws Exception{
        JSONObject json = new JSONObject();
        json.put("value", AESEncryption.cipher(applicationModel.getCipher(),value));
        json.put("name", applicationModel.getEmail());
        json.put("category", AESEncryption.cipher(applicationModel.getCipher(),category));
        json.put("title", AESEncryption.cipher(applicationModel.getCipher(),title));
        json.put("action", "delete");
        json.put("id", id);
        return json.toString();
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

        return json.toString();
    }

    /**
     *
     * @throws Exception
     */
    public static String readJSON() throws Exception{
        JSONObject json = new JSONObject();
        json.put("name", applicationModel.getEmail());
        json.put("action", "read");
        return json.toString();
    }


    public static long createRndID(){
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }

}
