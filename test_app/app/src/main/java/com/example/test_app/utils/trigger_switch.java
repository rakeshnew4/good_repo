package com.example.test_app.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class trigger_switch {
    public static String trigger(String room_id,String switch_id,String pos,String user_id){

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference rootRef = database.getReference();
        rootRef.child(user_id).child("rooms").child(room_id).child("switches").child(switch_id).child("pos").setValue(pos);
        System.out.println(room_id+switch_id+pos);
        return "success";
    }
}
