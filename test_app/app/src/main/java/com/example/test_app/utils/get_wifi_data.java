package com.example.test_app.utils;

import android.content.Context;

import com.example.test_app.CustomToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class get_wifi_data {
    public static String put_details(Context context,String wifi_name,String wifi_password) throws IOException {
        try {
            String data = file_manager.readFromFile(context, "data.txt");
            JSONObject new_data = new JSONObject(data);
            JSONObject wifi_data = new JSONObject();
            wifi_data.put("name",wifi_name);
            wifi_data.put("password",wifi_password);
            if (new_data.has("wifi_data")) {
                int wifi_data_length = new_data.getJSONObject("wifi_data").length()+1;
                String new_wifi_name = "wifi_data_"+wifi_data_length;
                new_data.getJSONObject("wifi_data").put(new_wifi_name, wifi_data);
                file_manager.writeToFile(new_data.toString(), context, "data.txt");
                System.out.println(new_data);
            } else {
                JSONObject wifi_object = new JSONObject();
                wifi_object.put("wifi_data_1",wifi_data);
                new_data.put("wifi_data", wifi_object);

                file_manager.writeToFile(new_data.toString(), context, "data.txt");
            }

            return "success";
        } catch (
                JSONException e) {
            CustomToast.showShortToast(context, "no data received");

        }
        return null;
    }
    public static JSONObject get_details(Context context) throws IOException {
        try {
            String data = file_manager.readFromFile(context, "data.txt");
            JSONObject new_data = new JSONObject(data);
           JSONObject wifi_data =  new_data.getJSONObject("wifi_data");
            return wifi_data;
        } catch (
                JSONException e) {

            CustomToast.showShortToast(context, "no data received");

        }
        return null;
    }
}
