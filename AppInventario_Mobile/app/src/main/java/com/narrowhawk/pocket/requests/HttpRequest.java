package com.narrowhawk.pocket.requests;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpRequest extends AsyncTask<String, Void, String> {
    public static String REQUEST_METHOD = null;
    public static String REQUEST_BODY = null;
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;

    public HttpRequest(String method, String body) {
        REQUEST_METHOD = method;
        REQUEST_BODY = body;
    }

    @Override
    protected String doInBackground(String... params){
        String stringUrl = params[0];
        String result;
        String inputLine;
        //String documento = null;
        String jsonResult = null;
        try {
            //Create a URL object holding our url
            URL myUrl = new URL(stringUrl);
            //Create a connection
            HttpURLConnection connection =(HttpURLConnection)
                    myUrl.openConnection();
            //Set methods and timeouts
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            if (REQUEST_METHOD.equals("PUT")) {
                connection.setRequestProperty("Content-Type", "application/json; utf-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                OutputStreamWriter out = new OutputStreamWriter(
                        connection.getOutputStream());
                out.write(REQUEST_BODY);
                out.close();
            } else {
                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
                JSONObject obj = new JSONObject(result);
                jsonResult = obj.toString();
            }
        }
        catch(IOException | JSONException e){
            e.printStackTrace();
            result = null;
        }
        return jsonResult;
    }
    protected void onPostExecute(String result){
        super.onPostExecute(result);
    }
}
