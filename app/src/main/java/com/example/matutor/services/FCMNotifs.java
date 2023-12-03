package com.example.matutor.services;

import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.matutor.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMNotifs extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("FCMNotifs", "onMessageReceived triggered");

        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Log.d("FCMNotifs", "Notification Title: " + title);
            Log.d("FCMNotifs", "Notification Body: " + body);

            // Create and show a notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channelId")
                    .setSmallIcon(R.drawable.notif)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0, builder.build());
        } else {
            Log.d("FCMNotifs", "No notification payload");
        }
    }

    @Override
    public void onNewToken(String token) {
        // Handle the new registration token
        Log.d("FCMNotifs", "Refreshed token: " + token);

        // You may want to send the new token to your server or update it in your app's user settings
    }
}