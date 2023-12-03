package com.example.matutor.services;

import android.content.Context;
import android.util.Log;

import com.example.matutor.R;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMNotifs extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        // Create and show a notification
        sendNotification(title, body, getApplicationContext());
    }

    @Override
    public void onNewToken(String token) {
        // Handle the new registration token
        Log.d("FCMNotifs", "Refreshed token: " + token);

        // You may want to send the new token to your server
        // or update it in your app's user settings
    }

    public static void sendNotification(String title, String body, Context context) {
        // Create and show a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channelId")
                .setSmallIcon(R.drawable.notif)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, builder.build());
    }
}
