package com.example.test_app.utils;

import android.content.Context;

import com.example.test_app.CustomToast;
import com.example.test_app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class get_switches_array_list {
    public static ArrayList get_all_switches_array(Context context, String room_name, String data) {
        final ArrayList<switchview> arrayList = new ArrayList<switchview>();

        try {
            System.out.println(data);
            JSONObject json_data = new JSONObject(data);
            JSONObject rooms = json_data.getJSONObject("rooms")
                    .getJSONObject(room_name).getJSONObject("switches");

            Iterator<String> iter = rooms.keys();
            while (iter.hasNext()) {
                String switch_id = iter.next();
                if (!switch_id.equalsIgnoreCase("dummy")) {
                    try {
                        JSONObject switch_data = rooms.getJSONObject(switch_id);
                        System.out.println("yes new");
                        Object switch_name = switch_data.get("name");
                        Object pos = switch_data.get("pos");
                        String pin = json_data.getJSONObject("pins").getString(switch_data.getString("pin"));
                        ;
                        String ip_number = switch_data.getString("ip_number");
                        arrayList.add(new switchview(R.drawable.bedroom, switch_name.toString(), room_name, ip_number, pos.toString(), R.drawable.timer_image, pin, switch_id));
                    } catch (JSONException e) {
                        CustomToast.showShortToast(context.getApplicationContext(), "unable to load "+switch_id);
                    }
                }
            }
        } catch (JSONException e) {

            CustomToast.showShortToast(context, "no data recieved");
        }
        return arrayList;
    }

    public static String update_switch_value(Context context, String room_name, String switch_name, String pos) throws IOException {
        String[] values = new String[5];

        if (pos.equalsIgnoreCase("0")) {
            pos = "1";
            return pos;
        } else if (pos.equalsIgnoreCase("1")) {
            pos = "0";
            return pos;
        }
        return pos;
    }
//    public static int get_switch_value(String pos){
//        if(pos.equalsIgnoreCase("OFF")){
//            return 0;
//        }
//        else {
//            return 1;
//        }
//
//    }
}
