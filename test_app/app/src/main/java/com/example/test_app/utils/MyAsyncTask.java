package com.example.test_app.utils;


import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;

import com.example.test_app.CustomToast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
public class MyAsyncTask extends AsyncTask<String, Void, String> {
    private Context context;
    private String url;
    private String requestMethod;
    private String requestBody;
    private MyAsyncTaskCallback callback;

    public MyAsyncTask(Context context1, String url1, String requestMethod, String requestBody, MyAsyncTaskCallback callback) {
        context = context1;
        url = url1;
        this.requestMethod = requestMethod;
        this.requestBody = requestBody;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... params) {
        String response = null;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        HttpURLConnection connection = null;
        try {
            URL apiUrl = new URL(url);
            connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod(requestMethod);
            connection.setConnectTimeout(10000); // 10 seconds
            connection.setReadTimeout(10000); // 10 seconds

            if ("POST".equalsIgnoreCase(requestMethod)) {
                // Set request properties for POST
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");

                // Write the request body to the output stream
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(requestBody.getBytes("UTF-8"));
                }
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String inputLine;
                    StringBuilder content = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    response = content.toString();
                }
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            response = null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        if (callback != null) {
            callback.onTaskComplete(result);
        }
    }

    public interface MyAsyncTaskCallback {
        JSONObject onTaskComplete(String result);
    }
}
