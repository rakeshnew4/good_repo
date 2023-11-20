
package com.example.test_app.utils;

public class switchview {

    // the resource ID for the imageView
    private int switch_image_id;
    private String pin_number;

    // TextView 1
    private String switch_id;
    private String switch_name;

    // TextView 1
    private String room_id;
    private String switch_pos;
    private int timer_image;
    private String ip_number;

    // create constructor to set the values for all the parameters of the each single view
    public switchview(int switch_image, String current_switch_name, String current_room_id, String my_ip_number, String switch_position, int timer_image_id, String current_pin_number, String current_switch_id) {
        switch_image_id = switch_image;
        switch_id = current_switch_id;
        switch_name = current_switch_name;
        ip_number = my_ip_number;
        room_id = current_room_id;
        pin_number = current_pin_number;
        switch_pos = switch_position;
        timer_image = timer_image_id;



    }

    // getter method for returning the ID of the imageview
    public int getSwitch_image_id() {
        return switch_image_id;
    }
    // getter method for returning the ID of the TextView 1
    public String getSwitchId() {
        return switch_id;
    }
    public String getRoom_id() {
        return room_id;
    }
    public String getPin_number() {
        return pin_number;
    }
    public String get_switch_pos() {
        return switch_pos;
    }

    public int getTimer_image() {
        return timer_image;
    }
    public String getIp_number() {
        return ip_number;
    }
    public String getSwitch_name() {
        return switch_name;
    }


}
