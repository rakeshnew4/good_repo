package com.example.test_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class Rooms_activity extends AppCompatActivity {

    private ListView listView;
    private NumbersViewAdapter adapter;
    private JSONObject jsonData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);
        listView = findViewById(R.id.listView);
        SharedPreferences sharedPreferences = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String server_address = sharedPreferences.getString("server_address", "");
        String local_address = sharedPreferences.getString("local_address", "");
        SharedPreferences my_pref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedUsername = my_pref.getString("uid", "");
        String json = null;
        final ArrayList<NumbersView> arrayList = new ArrayList<NumbersView>();
        fire_base_read.readData(getApplicationContext(), savedUsername, new fire_base_read.DataCallback() {
            @Override
            public void onDataLoaded(String data) throws JSONException {
                JSONObject jsonData = new JSONObject(data);
        try {
            JSONObject rooms = jsonData.getJSONObject("rooms");
            System.out.println(rooms);
            Iterator<String> iter = rooms.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                int switches_length = 0;
                String room_name = rooms.getJSONObject(key).get("name").toString();
                if (!room_name.equalsIgnoreCase("dummy")){
                    System.out.println(room_name);
                    JSONObject room_id = rooms.getJSONObject(key);
                    if (room_id.get("switches") instanceof JSONObject) {
                        switches_length = room_id.getJSONObject("switches").length()-1;
                    }
                    arrayList.add(new NumbersView(R.drawable.bedroom, room_name, key,switches_length+" switches"));
                }
            }
            NumbersViewAdapter numbersArrayAdapter = new NumbersViewAdapter(getApplicationContext(), arrayList);
            listView.setAdapter(numbersArrayAdapter);
        } catch (JSONException e) {
            System.out.println(e);
            CustomToast.showShortToast(getApplicationContext(),"no data recieved");
        }
            }
            @Override
            public void onError(String errorMessage) {
            }
        });
        NumbersViewAdapter numbersArrayAdapter = new NumbersViewAdapter(this, arrayList);
        listView.setAdapter(numbersArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NumbersView numbersView = numbersArrayAdapter.getItem(i);
                String room_id = numbersView.getNumbersInText();
                Intent switches  = new Intent(Rooms_activity.this, switches_activity.class);
                switches.putExtra("room_name",room_id);
                startActivity(switches);
            }
        });
        FloatingActionButton fab = findViewById(R.id.button_action);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add_switch_intent = new Intent(Rooms_activity.this,add_room.class);
                int REQUEST_CODE = 1;
                startActivityForResult(add_switch_intent, REQUEST_CODE);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int i, long id) {
                NumbersViewAdapter numbersArrayAdapter = new NumbersViewAdapter(getApplicationContext(), arrayList);
                adapter = numbersArrayAdapter;
                String room_id = arrayList.get(i).getNumbersInText();
                AlertDialog.Builder builder = new AlertDialog.Builder(Rooms_activity.this);
                builder.setTitle("Delete Item");
                builder.setMessage("Are you sure you want to delete this item?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference newref = database.getReference();
                            newref.child(savedUsername).child("rooms").child(room_id).removeValue();
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
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            recreate();
        }
    }
}