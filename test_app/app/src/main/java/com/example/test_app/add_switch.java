package com.example.test_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.test_app.utils.NumbersView;
import com.example.test_app.utils.call_api_http;
import com.example.test_app.utils.file_manager;
import com.example.test_app.utils.fire_base_read;
import com.example.test_app.utils.firebase_write;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class add_switch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_switch);

        EditText switch_name = findViewById(R.id.switch_name);
        Button save = findViewById(R.id.save);
        Intent intent = getIntent();
        String room_id = intent.getStringExtra("room_id");
        SharedPreferences sharedPreferences = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String server_address = sharedPreferences.getString("server_address", "");
        String local_address = sharedPreferences.getString("local_address", "");
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        Spinner pin_spinner = (Spinner) findViewById(R.id.spinner_pin);
        SharedPreferences my_pref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedUsername = my_pref.getString("uid", "");
        try {
//            String data = file_manager.readFromFile(getApplicationContext(),"data.txt");


            fire_base_read.readData(getApplicationContext(), savedUsername, new fire_base_read.DataCallback() {
                @Override
                public void onDataLoaded(String data) throws JSONException, IOException {
                    JSONObject json_data = new JSONObject(data);
                    JSONObject devices = json_data.getJSONObject("devices");

                    Iterator<String> iter = devices.keys();
                    ArrayList items = new ArrayList();
                    items.add("Select a device");
                    spinner.setPrompt("Select device id");
                    while (iter.hasNext()) {
                        String key = iter.next();
                        if(!key.equalsIgnoreCase("dummy")){
                            items.add(devices.getJSONObject(key).getString("name"));
                        }

                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, items);
                    spinner.setAdapter(adapter);
                    JSONObject pin_ids = json_data.getJSONObject("pins");
                    System.out.println(pin_ids);
                    Iterator<String> pin_numbers = pin_ids.keys();
                    ArrayList pins = new ArrayList();
                    pins.add("Select a switch number");
                    while (pin_numbers.hasNext()) {
                        String key = pin_numbers.next();
                        pins.add(key);
                    }

                    ArrayAdapter<String> pins_adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, pins);
                    pin_spinner.setAdapter(pins_adapter);
                }

                @Override
                public void onError(String errorMessage) {

                }
            });


        } finally {

        }


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String switch_1 = switch_name.getText().toString();
                if(switch_1.length()>1){
                    try {
                        final String[] device_name = {spinner.getSelectedItem().toString()};
                        String pin_number = pin_spinner.getSelectedItem().toString();


//                        String data = file_manager.readFromFile(getApplicationContext(),"data.txt");
                        fire_base_read.readData(getApplicationContext(), savedUsername, new fire_base_read.DataCallback() {
                            @Override
                            public void onDataLoaded(String data) throws JSONException, IOException {
                                JSONObject new_data = new JSONObject(data);
                                JSONObject devices = new_data.getJSONObject("devices");

                                Iterator<String> iter = devices.keys();
                                while (iter.hasNext()) {
                                    String key = iter.next();
                                    if(!key.equalsIgnoreCase("dummy")){
                                        String dev_name = devices.getJSONObject(key).getString("name");
                                        if(dev_name.equalsIgnoreCase(device_name[0])){
                                            device_name[0] = key;

                                        };
                                    }

                                }

                                String ip_number = new_data.getJSONObject("devices").getJSONObject(device_name[0]).getString("ip_number");
                              UUID uuid = UUID.randomUUID();
                                String switch_id = uuid.toString();
                                JSONObject new_switch_data = new JSONObject();
                                new_switch_data.put("pos", "0");
                                new_switch_data.put("name", switch_1);
                                new_switch_data.put("ip_number", ip_number);
                                new_switch_data.put("activity", "");

                                new_switch_data.put("pin", pin_number);

                                new_switch_data.put("device_id", device_name[0]);

                                JSONObject schedule_data = new JSONObject();
                                schedule_data.put("status", "OFF");
                                schedule_data.put("start_time", "");
                                schedule_data.put("end_time", "");
                                schedule_data.put("start_date", "");
                                schedule_data.put("end_date", "");
                                schedule_data.put("display_text", "10 minutes 10 sec");
                                new_switch_data.put("schedule", schedule_data);

                                new_data.getJSONObject("rooms").getJSONObject(room_id).getJSONObject("switches").put(switch_id, new_switch_data);
                                firebase_write.write_data(getApplicationContext(),savedUsername,new_data);
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            }

                            @Override
                            public void onError(String errorMessage) {

                            }
                        });

                    } finally {

                    }
                }
                else{
                    CustomToast.showShortToast(getApplicationContext(),"enter values");
                }
            }

        });
    }

}