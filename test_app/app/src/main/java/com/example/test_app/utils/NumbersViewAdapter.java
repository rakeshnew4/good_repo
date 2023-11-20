package com.example.test_app.utils;

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

import com.example.test_app.R;
import com.example.test_app.Rooms_activity;
import com.example.test_app.switches_activity;

import java.util.ArrayList;

public class NumbersViewAdapter extends ArrayAdapter<NumbersView> {
    private ImageView expandableImageView;
    private boolean isExpanded = false;
    private float originalX, originalY, originalWidth, originalHeight;
    // invoke the suitable constructor of the ArrayAdapter class
    public NumbersViewAdapter(@NonNull Context context, ArrayList<NumbersView> arrayList) {

        // pass the context and arrayList for the super
        // constructor of the ArrayAdapter class
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_view, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        NumbersView currentNumberPosition = getItem(position);

        // then according to the position of the view assign the desired image for the same
//        ImageView numbersImage = currentItemView.findViewById(R.id.imageView);
        expandableImageView = currentItemView.findViewById(R.id.imageView);
        assert currentNumberPosition != null;
        expandableImageView.setImageResource(currentNumberPosition.getNumbersImageId());

        // then according to the position of the view assign the desired TextView 1 for the same
        TextView textView1 = currentItemView.findViewById(R.id.textView1);
        textView1.setText(currentNumberPosition.getNumberInDigit());
        TextView textView2 = currentItemView.findViewById(R.id.textView2);
        textView2.setText(currentNumberPosition.get_text_2());
        String status = currentNumberPosition.get_text_2();
        if (status.equalsIgnoreCase("offline")) {
            textView2.setText(status);
            currentItemView.setBackgroundColor(androidx.cardview.R.color.cardview_shadow_end_color);
            currentItemView.setClickable(false);
        }
        return currentItemView;
    }
    public void updateData(ArrayList newData) {
        clear(); // Clear the current data in the adapter
        addAll(newData); // Add the new data to the adapter
        notifyDataSetChanged(); // Notify the ListView to refresh
    }
}





