package com.example.test_app.utils;

import android.content.Context;
import android.util.Log;

import com.example.test_app.CustomToast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class fire_base_read {
    public interface DataCallback {
        void onDataLoaded(String data) throws JSONException, IOException;
        void onError(String errorMessage);
    }

    public static void readData(Context context, String userId, DataCallback callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference usersReference = databaseReference.child(userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Handle data retrieval here
                String jsonData = "";
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if(dataSnapshot.hasChild(userId)){

                        Gson gson = new Gson();
                        if(dataSnapshot.child(userId).hasChild("rooms")){
                            jsonData = gson.toJson(dataSnapshot.child(userId).getValue());
                        }

                    }
                }
                try {
                    callback.onDataLoaded(jsonData);
                } catch (JSONException e) {
                    new_data.push_data.new_data(userId);
                    System.out.println(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                Log.e("Firebase", "Database Error: " + databaseError.getMessage());

                // Callback with the error message
                callback.onError(databaseError.getMessage());
            }
        });
    }

}
