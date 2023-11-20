package com.example.test_app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.example.test_app.utils.MyAsyncTask;
import com.example.test_app.utils.NumbersView;
import com.example.test_app.utils.call_api_http;
import com.example.test_app.utils.file_manager;
import com.example.test_app.utils.fire_base_read;
import com.example.test_app.utils.firebase_write;
import com.example.test_app.utils.get_switches_array_list;
import com.example.test_app.utils.get_wifi_data;
import com.example.test_app.utils.random_ip_select;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.os.Handler;
import android.text.InputType;
import android.text.format.Formatter;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class add_device extends AppCompatActivity {
    private boolean isPasswordVisible = false;
    private String ip_number;

    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        ProgressBar progress_bar = (ProgressBar) findViewById(R.id.progressBar1);
        EditText wifi_password = (EditText) findViewById(R.id.wifi_password);
        EditText wifi_name = (EditText) findViewById(R.id.wifi_name);
        EditText device = (EditText) findViewById(R.id.device_name);
        ImageView eyeImageView = (ImageView) findViewById(R.id.passwordToggle);
        Button save = (Button) findViewById(R.id.save);
        Button close_page = (Button) findViewById(R.id.close);
        SharedPreferences sharedPreferences = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String server_address = sharedPreferences.getString("server_address", "");
        String local_address = sharedPreferences.getString("local_address", "");
        SharedPreferences my_pref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedUsername = my_pref.getString("uid", "");
        progress_bar.setVisibility(View.GONE);
        String data = null;
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fire_base_read.readData(getApplicationContext(), savedUsername, new fire_base_read.DataCallback() {
                    @Override
                    public void onDataLoaded(String data) throws JSONException, IOException {
                        JSONObject json_data = new JSONObject(data);

                        JSONObject devices_data = json_data.getJSONObject("devices");
                        Iterator<String> iter = devices_data.keys();
                        List<Integer> existingIntegers = new ArrayList<>();
                        String ip_number = random_ip_select.get_number(existingIntegers);
                        String wifiname = wifi_name.getText().toString();
                        String password = wifi_password.getText().toString();
                        String device_name = device.getText().toString();
                        URL url = null;
                        try {
                            url = new URL("http://192.168.4.1/set_wifi");

                            String postData = "ssid=" + wifiname + "&pass=" + password + "&ip_number=" + ip_number+"&user_id=" + savedUsername
                                    +"&name=" + device_name;
                            if (wifiname.length() > 0 && device_name.length() > 0 && password.length() > 0) {
                                ExecutorService executorService = Executors.newFixedThreadPool(5);
                                URL finalUrl = url;
                                Runnable asyncHttpRequest = () -> {
                                    try {
                                        HttpURLConnection connection = (HttpURLConnection) finalUrl.openConnection();
                                        connection.setRequestMethod("POST");
                                        connection.setDoOutput(true);
                                        connection.setConnectTimeout(10000); // 10 seconds
                                        connection.setReadTimeout(10000);
                                        try (OutputStream os = connection.getOutputStream()) {
                                            byte[] input = postData.getBytes("utf-8");
                                            os.write(input, 0, input.length);
                                        }
                                        int responseCode = connection.getResponseCode();
                                        if (responseCode == 200) {
                                            UUID uuid = UUID.randomUUID();
                                            String device_id = uuid.toString();
                                            JSONObject device_details = new JSONObject();
                                            device_details.put("name", device_name);
                                            device_details.put("ssid", wifiname);
                                            device_details.put("password", password);
                                            device_details.put("ip_number", ip_number);
                                            device_details.put("status", "offline");
                                            devices_data.put(ip_number, device_details);
                                            json_data.put("devices", devices_data);
                                            JSONObject requests = json_data.getJSONObject("devices");
                                            JSONObject new_iot_requests_data = new JSONObject();
                                            new_iot_requests_data.put("counter", 0);
                                            System.out.println(ip_number);
                                            requests.put(ip_number, new_iot_requests_data);
                                            json_data.put("devices", requests);
                                            firebase_write.write_data(getApplicationContext(), savedUsername, json_data);
                                            Intent intent = new Intent();
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                };
                                executorService.submit(asyncHttpRequest);
                                executorService.shutdown();


                            }
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {

                    }

                });
            }
        });
        eyeImageView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        wifi_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        isPasswordVisible = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        wifi_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        isPasswordVisible = false;
                        break;
                }

                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        Handler handler = new Handler();
        final Runnable[] runnable = new Runnable[1];
        new AlertDialog.Builder(this)
                .setTitle("Confirm Exit")
                .setMessage("Are you sure you want to exit")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}