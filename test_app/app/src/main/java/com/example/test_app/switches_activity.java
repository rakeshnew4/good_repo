package com.example.test_app;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.test_app.utils.MyAsyncTask;
import com.example.test_app.utils.NumbersView;
import com.example.test_app.utils.NumbersViewAdapter;
import com.example.test_app.utils.call_api_http;
import com.example.test_app.utils.call_socket;
import com.example.test_app.utils.file_manager;
import com.example.test_app.utils.fire_base_read;
import com.example.test_app.utils.firebase_write;
import com.example.test_app.utils.switchesViewAdapter;
import com.example.test_app.utils.get_switches_array_list;
import com.example.test_app.utils.switchview;
import com.example.test_app.utils.trigger_switch;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class switches_activity extends AppCompatActivity {

    private ListView listView;
    private boolean isClickListenerEnabled = true;
    private switchesViewAdapter adapter;
    private ArrayList arrayList; // Replace YourItemType with your actual data type

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switches);
        listView = findViewById(R.id.listView);
        Intent intent = getIntent();
        String room_name = intent.getStringExtra("room_name");
        SharedPreferences sharedPreferences = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String server_address = sharedPreferences.getString("server_address", "");
        String local_address = sharedPreferences.getString("local_address", "");
        SharedPreferences my_pref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences at_home_value = getSharedPreferences("athome", Context.MODE_PRIVATE);
        String savedUsername = my_pref.getString("uid", "");
        String at_home = at_home_value.getString("at_home","" );
        String data = null;
        ProgressBar spinner = (ProgressBar) findViewById(R.id.progres);
        spinner.setVisibility(View.GONE);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = database.getReference().child(savedUsername);

        Intent switches  = new Intent(this, switches_activity.class);
        switches.putExtra("room_name",room_name);
        try {
            fire_base_read.readData(getApplicationContext(), savedUsername, new fire_base_read.DataCallback() {
                @Override
                public void onDataLoaded(String data) throws JSONException, IOException {
                    arrayList = get_switches_array_list.get_all_switches_array(getApplicationContext(), room_name, data);
                    adapter = new switchesViewAdapter(getApplicationContext(), arrayList);
                    listView.setAdapter(adapter);
                }
                @Override
                public void onError(String errorMessage) {
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                spinner.setVisibility(View.VISIBLE);
                                                Long d = (Long) adapter.getItemId(i) + 1;
                                                switchview numbersView = adapter.getItem(i);
                                                String switch_id = numbersView.getSwitchId();
                                                String ip_number = numbersView.getIp_number();
                                                String pin_number = numbersView.getPin_number();

                                                String switch_name = numbersView.getSwitch_name();

                                                fire_base_read.readData(getApplicationContext(), savedUsername, new fire_base_read.DataCallback() {
                                                    @Override
                                                    public void onDataLoaded(String data) throws JSONException, IOException {
                                                        JSONObject json_data = new JSONObject(data);
                                                        int pos = json_data.getJSONObject("devices")
                                                                .getJSONObject(ip_number).getJSONObject("pins_data").getInt(pin_number);
                                                        if (pos == 1) {
                                                            pos = 0;
                                                        } else {
                                                            pos = 1;
                                                        }
                                                        if (at_home.equalsIgnoreCase("no")) {

                                                            rootRef.child("devices").child(ip_number).child("pins_data").child(pin_number).setValue(pos);
                                                            int finalPos2 = pos;
                                                            database.getReference(savedUsername).child("devices").
                                                                    child(ip_number).child("counter").
                                                                    addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            System.out.println(snapshot);
                                                                            try {
                                                                                json_data.getJSONObject("rooms")
                                                                                        .getJSONObject(room_name).getJSONObject("switches").
                                                                                        getJSONObject(switch_id).put("pos", finalPos2);
                                                                                arrayList = get_switches_array_list.get_all_switches_array(getApplicationContext(), room_name, json_data.toString());
                                                                                adapter = new switchesViewAdapter(getApplicationContext(), arrayList);
                                                                                listView.setAdapter(adapter);
                                                                                firebase_write.write_data(getApplicationContext(),savedUsername,json_data);
                                                                                spinner.setVisibility(View.GONE);

                                                                            } catch (
                                                                                    JSONException e) {
                                                                                throw new RuntimeException(e);
                                                                            } catch (
                                                                                    IOException e) {
                                                                                throw new RuntimeException(e);
                                                                            }


                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                                        }
                                                                    });
                                                        } else {
                                                            if (pos == 1) {
                                                                pos = 0;
                                                            } else {
                                                                pos = 1;
                                                            }
                                                            System.out.println("entered 1");
                                                            String arduinoIP = "192.168.1." + ip_number; // Replace with your Arduino's IP address
                                                            URL url = new URL("http://" + arduinoIP + "/pin");
                                                            String postData = "pin=" + pin_number + "&value=" + pos;
                                                            ExecutorService executorService = Executors.newFixedThreadPool(5);
                                                            int finalPos = pos;
                                                            Runnable asyncHttpRequest = () -> {
                                                                try {

                                                                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                                                    connection.setRequestMethod("POST");
                                                                    connection.setDoOutput(true);
                                                                    try (OutputStream os = connection.getOutputStream()) {
                                                                        byte[] input = postData.getBytes("utf-8");
                                                                        os.write(input, 0, input.length);
                                                                    }
                                                                    int responseCode = connection.getResponseCode();
                                                                    if (responseCode == 200) {
                                                                        json_data.getJSONObject("rooms")
                                                                                .getJSONObject(room_name).getJSONObject("switches").
                                                                                getJSONObject(switch_id).put("pos", finalPos);
                                                                        ArrayList<NumbersView> updatedArrayList = get_switches_array_list.get_all_switches_array(getApplicationContext(), room_name, json_data.toString());
                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                spinner.setVisibility(View.GONE);
                                                                                adapter.updateData(updatedArrayList);
                                                                                listView.setAdapter(adapter);
                                                                            }
                                                                        });
                                                                        rootRef.child("rooms").child(room_name).child("switches").child(switch_id).child("pos").setValue(finalPos);
                                                                        rootRef.child(savedUsername).child("rooms").child(room_name).
                                                                                child("switches").child(switch_id).child("pos").setValue(finalPos);
                                                                        rootRef.child("devices").child(ip_number).child("counter").setValue(finalPos);
                                                                    }
                                                                    System.out.println("HTTP Response Code: " + responseCode);
                                                                    connection.disconnect();
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            };
                                                            executorService.submit(asyncHttpRequest);
                                                            executorService.shutdown();
                                                        }

                                                    }

                                                    @Override
                                                    public void onError(String errorMessage) {

                                                    }
                                                });
                                            }
                                        });

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                spinner.setVisibility(View.VISIBLE);
//                Long d = (Long) adapter.getItemId(i) + 1;
//                switchview numbersView = adapter.getItem(i);
//                String switch_id = numbersView.getSwitchId();
//                String ip_number = numbersView.getIp_number();
//                String pin_number = numbersView.getPin_number();
//                String pos = numbersView.get_switch_pos();
//                String switch_name = numbersView.getSwitch_name();
//                try {
//                    pos = get_switches_array_list.update_switch_value(getApplicationContext(),room_name,switch_id,pos);
//
////                    rootRef.child("devices").child(ip_number).child("counter").setValue(1);
//
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//
//                String finalPos = pos;
//
//                fire_base_read.readData(getApplicationContext(), savedUsername, new fire_base_read.DataCallback() {
//                    @Override
//                    public void onDataLoaded(String data) throws JSONException, IOException {
//                        JSONObject json_data = new JSONObject(data);
//                        if(at_home.equalsIgnoreCase("yes")) {
//
//                            json_data.getJSONObject("rooms")
//                                    .getJSONObject(room_name).getJSONObject("switches").
//                                    getJSONObject(switch_id).put("pos", finalPos);
//                            System.out.println(finalPos);
//                            int counter = json_data.getJSONObject("devices").getJSONObject(ip_number).getInt("counter");
//                            rootRef.child("devices").child(ip_number).child("pins_data").child(pin_number).setValue(finalPos)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            rootRef.addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                    int new_counter = Integer.parseInt(dataSnapshot.child("devices").child(ip_number).child("counter").getValue().toString());
//                                                    if(new_counter>counter){
//                                                        rootRef.child("rooms").child(room_name).child("switches").child(switch_id).child("pos").setValue(finalPos);
////                                                        rootRef.child(savedUsername).child("rooms").child(room_name).
////                                                                child("switches").child(switch_id).child("pos").setValue(finalPos);
//                                                        rootRef.child("devices").child(ip_number).child("counter").setValue(0);
//                                                        JSONObject newdt = new JSONObject((Map) dataSnapshot.getValue());
//                                                        ArrayList<NumbersView> updatedArrayList = get_switches_array_list.get_all_switches_array(getApplicationContext(), room_name, newdt.toString());
//                                                        runOnUiThread(new Runnable() {
//                                                            @Override
//                                                            public void run() {
//                                                                spinner.setVisibility(View.GONE);
//                                                                adapter.updateData(updatedArrayList);
//                                                                listView.setAdapter(adapter);
//                                                            }
//                                                        });
//                                                    }
//                                                }
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError error) {
//                                                }
//                                            });
//                                        }
//                                    });
//                        }
//                        else{
//                            System.out.println("entered 1");
//                            String arduinoIP = "192.168.1."+ip_number; // Replace with your Arduino's IP address
//                            URL url = new URL("http://" + arduinoIP + "/pin");
//                            String postData = "pin=" + pin_number + "&value=" + finalPos;
//                            ExecutorService executorService = Executors.newFixedThreadPool(5);
//                            Runnable asyncHttpRequest = () -> {
//                                try {
//
//                                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                                    connection.setRequestMethod("POST");
//                                    connection.setDoOutput(true);
//                                    try (OutputStream os = connection.getOutputStream()) {
//                                        byte[] input = postData.getBytes("utf-8");
//                                        os.write(input, 0, input.length);
//                                    }
//                                    int responseCode = connection.getResponseCode();
//                                    if (responseCode==200){
//                                        json_data.getJSONObject("rooms")
//                                                .getJSONObject(room_name).getJSONObject("switches").
//                                                getJSONObject(switch_id).put("pos", finalPos);
//                                        ArrayList<NumbersView> updatedArrayList = get_switches_array_list.get_all_switches_array(getApplicationContext(), room_name, json_data.toString());
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                spinner.setVisibility(View.GONE);
//                                                adapter.updateData(updatedArrayList);
//                                                listView.setAdapter(adapter);
//                                            }
//                                        });
//                                        rootRef.child("rooms").child(room_name).child("switches").child(switch_id).child("pos").setValue(finalPos);
//                                        rootRef.child(savedUsername).child("rooms").child(room_name).
//                                                child("switches").child(switch_id).child("pos").setValue(finalPos);
//                                        rootRef.child("devices").child(ip_number).child("counter").setValue(0);
//                                    }
//                                    System.out.println("HTTP Response Code: " + responseCode);
//                                    connection.disconnect();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            };
//                            executorService.submit(asyncHttpRequest);
//                            executorService.shutdown();
//                        }
//                    }
//                    @Override
//                    public void onError(String errorMessage) {
//                    }
//                });
//            }
//        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long id) {
                switchesViewAdapter new_adapter = new switchesViewAdapter(getApplicationContext(), arrayList);
                switchview numbersView = new_adapter.getItem(i);
                String switch_id = numbersView.getSwitchId();
                AlertDialog.Builder builder = new AlertDialog.Builder(switches_activity.this);
                builder.setTitle("Delete Item");
                builder.setMessage("Are you sure you want to delete this item?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference newref = database.getReference();
                            newref.child(savedUsername).child("rooms").child(room_name).child("switches").child(switch_id).removeValue();
                            fire_base_read.readData(getApplicationContext(), savedUsername, new fire_base_read.DataCallback() {
                                @Override
                                public void onDataLoaded(String data) throws JSONException, IOException {
                                    JSONObject new_data =  new JSONObject(data);
                                    firebase_write.write_data(getApplicationContext(),savedUsername,new_data);
                                    arrayList.remove(i);
                                    adapter.notifyDataSetChanged();
                                    recreate();
                                }
                                @Override
                                public void onError(String errorMessage) {

                                }
                            });

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing or dismiss the dialog
                    }
                });
                builder.show();

                return true; // Consume the long-press event
            }
        });

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add_switch_intent = new Intent(switches_activity.this,add_switch.class);
                add_switch_intent.putExtra("room_id",room_name);
                int REQUEST_CODE = 1;
                startActivityForResult(add_switch_intent, REQUEST_CODE);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("yes");
        if (resultCode == RESULT_OK) {
            recreate();
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

}
