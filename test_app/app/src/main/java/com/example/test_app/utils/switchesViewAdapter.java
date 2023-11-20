package com.example.test_app.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.test_app.CustomToast;
import com.example.test_app.R;
import com.example.test_app.schedule_activity;
import com.example.test_app.switch_details;
import com.example.test_app.switches_activity;

import java.util.ArrayList;

public class switchesViewAdapter extends ArrayAdapter<switchview> {
    private ImageView expandableImageView;
    private boolean isExpanded = false;
    private float originalX, originalY, originalWidth, originalHeight;
    // invoke the suitable constructor of the ArrayAdapter class
    public switchesViewAdapter(@NonNull Context context, ArrayList<switchview> arrayList) {

        // pass the context and arrayList for the super
        // constructor of the ArrayAdapter class
        super(context, 0, arrayList);
    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.custom_switches_view, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        switchview currentNumberPosition = getItem(position);
        ImageView timer_image = currentItemView.findViewById(R.id.timer_image);
        expandableImageView = currentItemView.findViewById(R.id.imageView);
        assert currentNumberPosition != null;
        expandableImageView.setImageResource(currentNumberPosition.getSwitch_image_id());

        // then according to the position of the view assign the desired TextView 1 for the same
        TextView textView1 = currentItemView.findViewById(R.id.textView1);
        textView1.setText(currentNumberPosition.getSwitch_name());

        if (currentNumberPosition.get_switch_pos().equalsIgnoreCase("1")){
            expandableImageView.setImageResource(R.drawable.bulbicon);
        }
        else{
            expandableImageView.setImageResource(R.drawable.off);
        }
        TextView textView2 = currentItemView.findViewById(R.id.textView2);
        textView2.setText(currentNumberPosition.getSwitch_name());

        timer_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), schedule_activity.class);
                intent.putExtra("switch_id", currentNumberPosition.getSwitchId());
                intent.putExtra("ip_number", currentNumberPosition.getIp_number());
                intent.putExtra("room_id", currentNumberPosition.getRoom_id());
                intent.putExtra("pin_number", currentNumberPosition.getPin_number());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);

            }
        });
        return currentItemView;
    }

    public void updateData(ArrayList newData) {
        clear(); // Clear the current data in the adapter
        addAll(newData); // Add the new data to the adapter
        notifyDataSetChanged(); // Notify the ListView to refresh
    }

}


