package com.example.test_app.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.NoSuchFileException;

public class file_manager {
    public static String writeToFile(String data, Context context, String file_name) {
        OutputStreamWriter outputStreamWriter = null;
        try {
            outputStreamWriter = new OutputStreamWriter(context.openFileOutput(file_name, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            return "yes"; // Return "yes" if writing is successful
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            return "no"; // Return "no" if an error occurs during writing
        } finally {
            try {
                if (outputStreamWriter != null) {
                    outputStreamWriter.close(); // Close the file in the finally block
                }
            } catch (IOException e) {
                Log.e("Exception", "Error closing file: " + e.toString());
            }
        }
    }


    public static String readFromFile(Context context, String file_name) throws IOException {
        InputStream inputStream;
        String ret = "";
        inputStream = context.openFileInput(file_name);

        try {
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
}
