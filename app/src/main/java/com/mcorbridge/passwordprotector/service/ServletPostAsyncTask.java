package com.mcorbridge.passwordprotector.service;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;

import com.mcorbridge.passwordprotector.interfaces.IPasswordActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mike on 12/27/2014.
 * copyright Michael D. Corbridge
 */
public class ServletPostAsyncTask extends AsyncTask<Pair<Context, String>, Void, String> {
    private Context context;

    @Override
    protected String doInBackground(Pair<Context, String>... params){
        context = params[0].first;
        String name = params[0].second;

        HttpClient httpClient = new DefaultHttpClient();

        //String servletEndpoint = "http://192.168.0.10:8080/hello";
        String servletEndpoint = "http://plasma-circle-815.appspot.com/hello";

        // allows view to show which endpoint client currently using
        alertServletEndpoint(servletEndpoint.equals("http://192.168.0.10:8080/hello")? "dev" : "prod" );

        HttpPost httpPost = new HttpPost(servletEndpoint);

        try {
            // Add name data to request
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("json", name));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity());
            }
            return "Error: " + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase();

        } catch (ClientProtocolException e) {
            return e.getMessage();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    /**
     * any class that uses this class and implements IPasswordActivity MUST be included
     * in the following if-else or it will not be included in the onPostExecute
     */
    protected void onPostExecute(final String result){
        //Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        Activity main = (Activity)context;

        String className = main.getClass().getName();

        if( className.contains("CreateActivity") ||
            className.contains("ReadActivity")   ||
            className.contains("UpdateActivity") ||
            className.contains("DeleteActivity") ||
            className.contains("PasswordDataActivity")){
                ((IPasswordActivity) main).processResults(result);
        }
    }

    //stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi
    // You have to move the portion of the background task that updates the ui onto the main thread. There is a simple piece of code for this:
    protected void alertServletEndpoint(String servletEndpoint){

//        final String sep = servletEndpoint;
//
//        final Activity main = (Activity)context;
//
//        ((PasswordDataActivity) main).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                ((PasswordDataActivity) main).alertServletEndpoint(sep);
//            }
//        });
   }
}
