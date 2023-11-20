package com.example.test_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.test_app.utils.NumbersView;
import com.example.test_app.utils.NumbersViewAdapter;
import com.example.test_app.utils.call_api_http;
import com.example.test_app.utils.file_manager;
import com.example.test_app.utils.fire_base_read;
import com.example.test_app.utils.firebase_write;
import com.example.test_app.utils.switchesViewAdapter;
import com.example.test_app.utils.switchview;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class devices_list extends AppCompatActivity {

    private ListView listView;
    private NumbersViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_list);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        FloatingActionButton reload = findViewById(R.id.reloadButton);
        listView = findViewById(R.id.listView);
        SharedPreferences sharedPreferences = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences my_pref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedUsername = my_pref.getString("uid", "");
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(savedUsername);
        myRef.keepSynced(true);



        final ArrayList<NumbersView> arrayList = new ArrayList<NumbersView>();
        fire_base_read.readData(getApplicationContext(), savedUsername, new fire_base_read.DataCallback() {
            @Override
            public void onDataLoaded(String data) throws JSONException, IOException {
                JSONObject json_data = new JSONObject(data);
                JSONObject devices = json_data.getJSONObject("devices");
                Iterator<String> iter = devices.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    if (!key.equalsIgnoreCase("dummy")) {
                        if (devices.getJSONObject(key).has("name")) {
                            System.out.println(key + devices.getJSONObject(key));
                            Object device_name = devices.getJSONObject(key).get("name");
                            Object wifi_name = devices.getJSONObject(key).get("ssid").toString();
                            String device_status = devices.getJSONObject(key).get("status").toString();
                            System.out.println(key + device_status);
                            arrayList.add(new NumbersView(R.drawable.bedroom, device_name.toString(), key, device_status));
                            myRef.child("devices").child(key).child("counter").setValue("1");

                        }
                    }

                }
                NumbersViewAdapter numbersArrayAdapter = new NumbersViewAdapter(getApplicationContext(), arrayList);
                listView.setAdapter(numbersArrayAdapter);
            }
            @Override
            public void onError(String errorMessage) {

            }
        });

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fire_base_read.readData(getApplicationContext(), savedUsername, new fire_base_read.DataCallback() {
                    @Override
                    public void onDataLoaded(String data) throws JSONException, IOException {
                        JSONObject json_data = new JSONObject(data);
                        JSONObject devices = json_data.getJSONObject("devices");
                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference(savedUsername);
                        myRef.keepSynced(true);
                        Iterator<String> iter = devices.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();
                            if (!key.equalsIgnoreCase("dummy")){
                                System.out.println(key+myRef);
                                myRef.child("devices").child(key).child("status").setValue("offline");
                                myRef.child("devices").child(key).child("counter").setValue("1");
                            }
                        }




                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                           recreate();
                            }
                        }, 1000); // 2000 milliseconds = 2 seconds

                    }

                    @Override
                    public void onError(String errorMessage) {

                    }
                });
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                String ipString = String.format(
                        "%d.%d.%d.%d",
                        (ipAddress & 0xff),
                        (ipAddress >> 8 & 0xff),
                        (ipAddress >> 16 & 0xff),
                        (ipAddress >> 24 & 0xff)
                );
                if (ipString.startsWith("192.168.4.")) {
                    Intent add_switch_intent = new Intent(devices_list.this, add_device.class);
                    int REQUEST_CODE = 1;
                    startActivityForResult(add_switch_intent, REQUEST_CODE);

                }
                else{
                    CustomToast.showShortToast(getApplicationContext(),"please connect to a device");
                    startActivity(new Intent("android.settings.panel.action.INTERNET_CONNECTIVITY"));
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int i, long id) {
                NumbersViewAdapter numbersArrayAdapter = new NumbersViewAdapter(getApplicationContext(), arrayList);
                adapter = numbersArrayAdapter;

                String device_id = arrayList.get(i).getNumbersInText();
                AlertDialog.Builder builder = new AlertDialog.Builder(devices_list.this);
                builder.setTitle("Action");
                builder.setMessage("Please proceed to");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fire_base_read.readData(getApplicationContext(), savedUsername, new fire_base_read.DataCallback() {
                            @Override
                            public void onDataLoaded(String data) throws JSONException, IOException {

                                JSONObject json_data = new JSONObject(data);
                  json_data.getJSONObject("devices").remove(device_id);
                                firebase_write.write_data(getApplicationContext(), savedUsername, json_data);
                                arrayList.remove(i);
                                adapter.notifyDataSetChanged();
                                recreate();
                            }

                            @Override
                            public void onError(String errorMessage) {

                            }
                        });
                    }
                });
                builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing or dismiss the dialog
                    }
                });
                builder.show();

                return true; // Consume the long-press event
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("yes");
        // Check if the result is from the second activity
        if (resultCode == RESULT_OK) {

            recreate();
        }

    }


}


