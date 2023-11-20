package com.example.test_app;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test_app.utils.fire_base_read;
import com.example.test_app.utils.firebase_write;
import com.example.test_app.utils.time_set;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class schedule_activity extends AppCompatActivity {
    String schedule_status = "true";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String switch_id = intent.getStringExtra("switch_id");
        String ip_number = intent.getStringExtra("ip_number");
        String pin_number = intent.getStringExtra("pin_number");
        String room_id = intent.getStringExtra("room_id");
        setContentView(R.layout.activity_schedule);
        Button schedule_button =(Button)findViewById(R.id.schedule_bt);
        TextView tv = (TextView) findViewById(R.id.display_schedule);
        Button start_time=(Button)findViewById(R.id.start_date);
        Button end_time=(Button)findViewById(R.id.end_time);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_1);
        SharedPreferences my_pref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedUsername = my_pref.getString("uid", "");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());
        start_time.setFocusable(true);
        start_time.setText(currentDateTime);
        end_time.setText(currentDateTime);
        start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String status = time_set.setDateTime(schedule_activity.this, start_time);

                    }
                });
            }
        });
        end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String status = time_set.setDateTime(schedule_activity.this, end_time);

                    }
                });
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton radioButton = (RadioButton)radioGroup.findViewById(checkedId);
                if (checkedId== R.id.schedule_on) {
                    schedule_status = "true";
                }
                if(checkedId==R.id.schedule_off) {
                    schedule_status = "false";
                }
            }
        });
        schedule_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (schedule_status.equals("true")) {
                        fire_base_read.readData(getApplicationContext(), savedUsername, new fire_base_read.DataCallback() {
                            @Override
                            public void onDataLoaded(String data) throws JSONException, IOException {
                                JSONObject json_data = new JSONObject(data);
                                JSONObject schedule_data = new JSONObject();
                                String start = start_time.getText().toString();
                                String end = end_time.getText().toString();
                                JSONObject pin_start_data = new JSONObject();
                                JSONObject pin_end_data = new JSONObject();
                                pin_start_data.put(pin_number,1);
                                pin_end_data.put(pin_number,0);


                                if(json_data.getJSONObject("devices").getJSONObject(ip_number).has("schedule")) {
                                    if(json_data.getJSONObject("devices").getJSONObject(ip_number).getJSONObject("schedule").has(start)){
                                        json_data.getJSONObject("devices").getJSONObject(ip_number).
                                                getJSONObject("schedule").getJSONObject(start).put(pin_number,1);
                                    }else{
                                        json_data.getJSONObject("devices").getJSONObject(ip_number).
                                                getJSONObject("schedule").put(start, pin_start_data);
                                    }
                                    if(json_data.getJSONObject("devices").getJSONObject(ip_number).getJSONObject("schedule").has(end)){
                                        json_data.getJSONObject("devices").getJSONObject(ip_number).
                                                getJSONObject("schedule").getJSONObject(end).put(pin_number,0);
                                    }else{
                                        json_data.getJSONObject("devices").getJSONObject(ip_number).
                                                getJSONObject("schedule").put(end, pin_end_data);
                                    }
                                }
                                else{
                                    json_data.getJSONObject("devices").getJSONObject(ip_number).
                                            put("schedule",new JSONObject());
                                    json_data.getJSONObject("devices").getJSONObject(ip_number).
                                            getJSONObject("schedule").put(start, pin_start_data);
                                    json_data.getJSONObject("devices").getJSONObject(ip_number).
                                            getJSONObject("schedule").put(end, pin_end_data);
                                }
//                                if(json_data.getJSONObject("devices").getJSONObject(ip_number).
//                                        getJSONObject("schedule").has(end)) {
//                                    json_data.getJSONObject("devices").getJSONObject(ip_number).
//                                            getJSONObject("schedule").getJSONObject(end).put(pin_number, 1);
//                                }else{
//                                    json_data.getJSONObject("devices").getJSONObject(ip_number).
//                                            getJSONObject("schedule").put("start_time",start);
//                                    json_data.getJSONObject("devices").getJSONObject(ip_number).
//                                            getJSONObject("schedule").getJSONObject(start).put(pin_number, 1);
//                                }
                                json_data.getJSONObject("rooms").getJSONObject(room_id).getJSONObject("switches")
                                .getJSONObject(switch_id).put("schedule",schedule_data);
                                JSONObject switch_id_data = new JSONObject();
                                switch_id_data.put(switch_id,schedule_data);

                                firebase_write.write_data(getApplicationContext(), savedUsername, json_data);
                                finish();
                            }
                            @Override
                            public void onError(String errorMessage) {

                            }
                        });
                    } else {

                    }
                } finally {

                }

            }
        });
    }

}