package com.example.test_app.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.test_app.schedule_activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class time_set {

    public static String setDateTime(Activity activity, TextView timeView) {
        final Calendar calendar = Calendar.getInstance();

        // Initialize date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                activity,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Set the selected date to the calendar
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Initialize time picker dialog
                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                activity,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        // Set the selected time to the calendar
                                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calendar.set(Calendar.MINUTE, minute);

                                        // Format the selected date and time
                                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault());
                                        String formattedDateTime = sdf.format(calendar.getTime());

                                        Intent intent = new Intent(activity, schedule_activity.class);
                                        timeView.setText(formattedDateTime);
                                    }
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                        );

                        // Show the time picker dialog
                        timePickerDialog.show();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Show the date picker dialog
        datePickerDialog.show();

        return "success";
    }
}
