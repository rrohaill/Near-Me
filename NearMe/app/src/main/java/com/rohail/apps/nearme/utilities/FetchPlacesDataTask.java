package com.rohail.apps.nearme.utilities;

/**
 * Created by Rohail on 2/21/2016.
 */

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A class, to download Google Places JASON Data
 */
public class FetchPlacesDataTask extends AsyncTask<String, Integer, String> {

    String data = null;
    Context context;

    public FetchPlacesDataTask(Context context) {
        this.context=context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        com.rohail.apps.nearme.utilities.StaticData.progressDialog = new ProgressDialog(context);
        com.rohail.apps.nearme.utilities.StaticData.progressDialog.setMessage("Loading Nearby Places\nPlease wait...");
        com.rohail.apps.nearme.utilities.StaticData.progressDialog.setIndeterminate(true);
        com.rohail.apps.nearme.utilities.StaticData.progressDialog.show();
    }

    // Invoked by execute() method of this class object
    @Override
    protected String doInBackground(String... url) {
        try {

            data = downloadFromUrl(url[0]);
            com.rohail.apps.nearme.utilities.Logger.i(data+"");
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return data;
    }

    // Executed after the complete execution of doInBackground() method
    @Override
    protected void onPostExecute(String result) {
        com.rohail.apps.nearme.utilities.ParsePlacesDataTask parsePlacesDataTask = new com.rohail.apps.nearme.utilities.ParsePlacesDataTask(context);

        // Start parsing the Google places in JSON format
        // Invokes the "doInBackground()" method of the class ParseTask
        parsePlacesDataTask.execute(result);
//        StaticData.progressDialog.hide();
    }

    /**
     * A method to download json data from url
     */
    @SuppressLint("LongLogTag")
    private String downloadFromUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

}
