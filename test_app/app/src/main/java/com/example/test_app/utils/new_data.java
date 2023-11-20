package com.example.test_app.utils;

import android.annotation.SuppressLint;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class new_data {
    public static class Device {
        public String id= "dummy";
        public String ip= "124";
        public String password= "dummy";
        public String wifi_name= "dummy";
    }

    public static class Pin {
        public String l1 = "16";
        public String l2 = "5";
        public String l3 = "4";
        public String l4= "14";
        public String l5 = "0";
        public String l6 = "2";
    }

    public static class Switch {
        public String activity = "dummy";
        public String ip_number= "124";
        public String name= "dummy";
        public String pin= "dummy";
        public String pos= "dummy";
        public Schedule schedule;
        public String user= "dummy";
    }

    public static class Schedule {
        public String display_text= "dummy";
        public String end_date= "dummy";
        public String end_time= "dummy";
        public String start_date= "dummy";
        public String start_time= "dummy";
        public String status= "dummy";
    }
    public static class rooms_data {
        public String name= "dummy";
        public String changed= "true";
    }
    public static class push_data{
        @SuppressLint("SuspiciousIndentation")
        public static void new_data(String userId){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference rootReference = database.getReference();

            // Create instances of the data structure
            new_data.Device device = new new_data.Device();
            new_data.rooms_data rooms = new new_data.rooms_data();
            new_data.Pin pin = new new_data.Pin();
            new_data.Switch switchData = new new_data.Switch();
            new_data.Schedule schedule = new new_data.Schedule();
            device.id = "dummy";
            device.ip = "124";
            device.password = "dummy";
            device.wifi_name = "dummy";
            rootReference.child(userId).child("rooms").child("dummy").setValue(rooms);
            rootReference.child(userId).child("devices").child("dummy").setValue(device);
            rootReference.child(userId).child("pins").setValue(pin);
            rootReference.child(userId).child("rooms").child("dummy").child("switches").child("dummy").setValue(switchData);
            rootReference.child(userId).child("rooms").child("dummy").child("switches").child("dummy").child("schedule").setValue(schedule);
        }
    }

}
