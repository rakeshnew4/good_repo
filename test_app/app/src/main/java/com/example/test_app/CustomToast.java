package com.example.test_app;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class CustomToast {



    private static final int TOAST_DURATION_SHORT = 1500; // 2 seconds

    public static void showShortToast(Context context, CharSequence message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();

        // Create a Handler to dismiss the toast after the desired duration
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            toast.cancel();

        }, TOAST_DURATION_SHORT);
    }


}
