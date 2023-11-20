package com.example.test_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.test_app.utils.NumbersView;
import com.example.test_app.utils.NumbersViewAdapter;
import com.example.test_app.utils.call_api_http;
import com.example.test_app.utils.file_manager;
import com.example.test_app.utils.fire_base_read;
import com.example.test_app.utils.firebase_write;
import com.example.test_app.utils.new_data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;



public class add_room extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);
        UUID uuid = UUID.randomUUID();
        String room_id = uuid.toString();
        EditText room_name = findViewById(R.id.room_name);
        Button save = findViewById(R.id.save);
        SharedPreferences sharedPreferences = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String server_address = sharedPreferences.getString("server_address", "");
        String local_address = sharedPreferences.getString("local_address", "");
        SharedPreferences my_pref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedUsername = my_pref.getString("uid", "");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String room_name_1 = room_name.getText().toString();
                if (room_name_1.length() > 1) {
                    try {
                        fire_base_read.readData(getApplicationContext(), savedUsername, new fire_base_read.DataCallback() {
                            @Override
                            public void onDataLoaded(String data) throws JSONException, IOException {
                                JSONObject new_data = new JSONObject(data);
                                JSONObject new_room_data = new JSONObject();
                                new_room_data.put("name", room_name_1);
                                new_room_data.put("changed","true");
                                JSONObject switch_data = new JSONObject();
                                switch_data.put("dummy","dummy");
                                new_room_data.put("switches", switch_data);
                                try{
                                    new_data.getJSONObject("rooms").put(room_id, new_room_data);
                                }
                                catch (JSONException e){
                                    new_data.put("rooms",new JSONObject());
                                    new_data.getJSONObject("rooms").put(room_id, new_room_data);
                                }
                                firebase_write.write_data(getApplicationContext(),savedUsername,new_data);

                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                            @Override
                            public void onError(String errorMessage) {

                            }
                        });

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }



}