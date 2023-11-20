package com.example.test_app.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import com.example.test_app.CustomToast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class call_socket {
    public String post_call() {
        String arduinoIP = "192.168.1.100"; // Replace with your Arduino's IP address
        String ssid = "YourSSID"; // Replace with your SSID
        String password = "YourPassword"; // Replace with your password
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        Runnable asyncHttpRequest = () -> {
            try {
                URL url = new URL("http://" + arduinoIP + "/set_wifi");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                String postData = "ssid=" + ssid + "&pass=" + password;
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = postData.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                int responseCode = connection.getResponseCode();
                System.out.println("HTTP Response Code: " + responseCode);
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        executorService.submit(asyncHttpRequest);
        executorService.shutdown();
return "success";
    }
}
