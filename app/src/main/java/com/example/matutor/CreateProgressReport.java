package com.example.matutor;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class CreateProgressReport extends AppCompatActivity {

    private Button close, send;
    private EditText postDescInput;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // removes status bar
        setContentView(R.layout.activity_create_progress_report);

        close = findViewById(R.id.closeButton);
        send = findViewById(R.id.sendReportButton);
        postDescInput = findViewById(R.id.postDescInput);
        firestore = FirebaseFirestore.getInstance();

        //close review page
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeConfirmation();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String progressReport = postDescInput.getText().toString().trim();
                if (!progressReport.isEmpty()) {
                    // Fetch email and password from Firestore
                    new FetchEmailAndPasswordTask(progressReport).execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter progress report", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class FetchEmailAndPasswordTask extends AsyncTask<Void, Void, Void> {

        private String email;
        private String password;
        private String progressReport;

        public FetchEmailAndPasswordTask(String progressReport) {
            this.progressReport = progressReport;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            firestore.collection("user_learner").document("user_learner") // Replace with the actual user type
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                email = documentSnapshot.getString("email");
                                password = documentSnapshot.getString("password");

                                // Execute the SendMailTask asynchronously
                                Executor executor = Executors.newSingleThreadExecutor();
                                executor.execute(() -> {
                                    new SendMailTask(CreateProgressReport.this, email, password, email, email, "Progress Report", progressReport).execute();
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                        }
                    });

            return null;
        }
    }

    private class SendMailTask extends AsyncTask<Void, Void, Void> {

        private Context context;
        private String tutorEmail;
        private String appPassword;
        private String recipientEmail;
        private String guardianEmail;
        private String subject;
        private String messageBody;

        public SendMailTask(Context context, String tutorEmail, String appPassword,
                            String recipientEmail, String guardianEmail, String subject, String messageBody) {
            this.context = context;
            this.tutorEmail = tutorEmail;
            this.appPassword = appPassword;
            this.recipientEmail = recipientEmail;
            this.guardianEmail = guardianEmail;
            this.subject = subject;
            this.messageBody = messageBody;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Setup mail server properties
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "465");

                // Creating a new session
                Session session = Session.getDefaultInstance(props,
                        new javax.mail.Authenticator() {
                            // Authenticating the password
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(tutorEmail, appPassword);
                            }
                        });

                // Creating MimeMessage object
                MimeMessage mimeMessage = new MimeMessage(session);
                // Set the sender address
                mimeMessage.setFrom(new InternetAddress(tutorEmail));
                // Set the recipient address
                mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
                mimeMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(guardianEmail));
                // Set email subject
                mimeMessage.setSubject(subject);
                // Set email message
                mimeMessage.setText(messageBody);

                // Send email
                Transport.send(mimeMessage);

            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Perform any UI-related operations after sending the email
            // For example, show a toast indicating the email has been sent
            Toast.makeText(context, "Email sent successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void closeConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Discard progress report?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), BookingsTutor.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}