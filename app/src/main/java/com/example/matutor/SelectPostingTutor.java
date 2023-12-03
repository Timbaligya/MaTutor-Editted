package com.example.matutor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.example.matutor.services.FCMNotifs;

public class SelectPostingTutor extends AppCompatActivity {

    Button interested, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_select_posting_tutor);

        interested = findViewById(R.id.interestedButton);
        cancel = findViewById(R.id.cancelButton);

        // interested button
        interested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Request FCM token
                requestFCMToken();

                // Show a toast indicating that the request is sent
                Toast.makeText(getApplicationContext(), "Request sent!", Toast.LENGTH_SHORT).show();

                // Send notification
                FCMNotifs.sendNotification("Post Response", "A tutor is interested in your post!", getApplicationContext());

                // Handle other actions or navigate to a different screen if needed
                onBackPressed();
            }
        });

        // cancel button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PostingsTutor.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), PostingsTutor.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
        finish();
    }

    private void requestFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        // Handle token retrieval failure
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    // Log and handle the token as needed
                    Log.d("FCMNotifs", "FCM Token: " + token);
                });
    }
}
