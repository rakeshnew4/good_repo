package com.example.test_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.example.test_app.utils.MyAsyncTask;
import com.example.test_app.utils.NumbersView;
import com.example.test_app.utils.NumbersViewAdapter;
import com.example.test_app.utils.call_api_http;
import com.example.test_app.utils.call_socket;
import com.example.test_app.utils.file_manager;
import com.example.test_app.utils.fire_base_read;
import com.example.test_app.utils.firebase_write;
import com.example.test_app.utils.get_switches_array_list;
import com.example.test_app.utils.network_check;
import com.example.test_app.utils.random_ip_select;
import com.example.test_app.utils.switchesViewAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    public String radio_button_value="yes";
    private Handler handler = new Handler();
    private Runnable runnable;
    private final int delay = 5000;
    private ProgressBar spinner;
    private Boolean loaded = false;
    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.xml.main_menu, menu);


        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settings = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        boolean is_net_available = network_check.isNetworkAvailable(getApplicationContext());
        if(!is_net_available){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            radio_button_value = "yes";
        }
        else{
            radio_button_value = "no";
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button enter_button = (Button) findViewById(R.id.btnOFF);
        Button setup_wifi = (Button) findViewById(R.id.wifi_setup);
        RadioGroup radioGroup;
        radioGroup = (RadioGroup)findViewById(R.id.groupradio);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
//        SharedPreferences sharedPreferences = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        String server_address = sharedPreferences.getString("server_address", "");
//        String local_address = sharedPreferences.getString("local_address", "");
//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        SharedPreferences my_pref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//        String savedUsername = my_pref.getString("uid", "");


//        rootRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String data = dataSnapshot.child(savedUsername).getValue().toString();
//                JSONObject new_data  = null;
//                try {
//                    new_data = new JSONObject(data);
//                    firebase_write.write_data(getApplicationContext(),savedUsername,new_data);
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });

//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                String data = call_api_http.call("http://192.168.1.12:80","data",getApplicationContext());
//                CustomToast.showShortToast(getApplicationContext(),resp);
//                String status = load_data.load(getApplicationContext(),radio_button_value,ip);
//                if (status=="loaded"){
//                    loaded = true;
//                }
//                else {
//                    loaded= false;
//                    CustomToast.showShortToast(getApplicationContext(),"please connect to device or internet");
//                }
//                handler.postDelayed(this, delay);
//            }
//        };
//
//        handler.postDelayed(runnable, delay);



        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton radioButton = (RadioButton)radioGroup.findViewById(checkedId);
                if (checkedId== R.id.radia_no) {
                    radio_button_value = "no";

                }
                if(checkedId==R.id.radia_yes) {
                    radio_button_value = "yes";
                }
            }
        });

        enter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);
                try {
                    SharedPreferences sharedPreferences = getSharedPreferences("athome", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("at_home", radio_button_value);
                    editor.apply();

                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            spinner.setVisibility(View.GONE);

                            Intent device_control_intent = new Intent(MainActivity.this, Rooms_activity.class);
                            startActivity(device_control_intent);
                        }

                    }, 200);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        setup_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent wifi_setup_intent = new Intent(MainActivity.this,devices_list.class);
                startActivity(wifi_setup_intent);
                            }
        });




    }
}