package com.example.test_app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.test_app.CustomToast;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileReader;

public class firebase_write {
    public static  String write_data(Context context,String username,JSONObject data) throws IOException, JSONException {
        Type mapType = new com.google.gson.reflect.TypeToken<Map<String, Object>>() {}.getType();
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(data.toString(), mapType);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference rootRef = database.getReference();

        DatabaseReference roomsRef = rootRef.child(username);
        roomsRef.setValue(map);
//        roomsRef.child("509cb3de-8756-45bb-a608-fc5ba9fcf24c").child("switches").push().setValue(switch1);
//        roomsRef.setValue(new JSONObject().toString());
//        Map<String, Object> data = new HashMap<>();
//        Map<String, Object> switches_data = new HashMap<>();
//        switches_data.put("email", "johndoe@example.com");
//        data.put("bedroom", switches_data);
//        DatabaseReference newUserReference = roomsRef.push();
//        newUserReference.setValue(data);




        return null;
    }

}
