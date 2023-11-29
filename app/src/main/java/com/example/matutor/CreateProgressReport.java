package com.example.matutor;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.lang.ref.WeakReference;
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

    Button close, send;
    EditText progressReportEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // removes status bar
        setContentView(R.layout.activity_create_progress_report);

        close = findViewById(R.id.closeButton);
        send = findViewById(R.id.sendReportButton);
        progressReportEditText = findViewById(R.id.postDescInput);

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
                String progressReport = progressReportEditText.getText().toString().trim();
                if (!progressReport.isEmpty()) {
                    // Replace with your email server details
                    String senderEmail = "your_email@gmail.com";
                    String senderPassword = "your_email_password";
                    String recipientEmail = "learner_email@gmail.com"; // replace with learner's email
                    String guardianEmail = "guardian_email@gmail.com"; // replace with guardian's email

                    String subject = "Progress Report";
                    String messageBody = progressReport;

                    Executor executor = Executors.newSingleThreadExecutor();
                    executor.execute(new SendMailTask(CreateProgressReport.this, senderEmail, senderPassword, recipientEmail, guardianEmail, subject, messageBody));
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter progress report", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), BookingsTutor.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void closeConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage("Discard post-session note?");
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

    private static class SendMailTask implements Runnable {
        private WeakReference<CreateProgressReport> activityReference;
        private ProgressDialog progressDialog;
        private String senderEmail;
        private String senderPassword;
        private String recipientEmail;
        private String guardianEmail;
        private String subject;
        private String messageBody;

        SendMailTask(CreateProgressReport activity, String senderEmail, String senderPassword, String recipientEmail, String guardianEmail, String subject, String messageBody) {
            this.activityReference = new WeakReference<>(activity);
            this.senderEmail = senderEmail;
            this.senderPassword = senderPassword;
            this.recipientEmail = recipientEmail;
            this.guardianEmail = guardianEmail;
            this.subject = subject;
            this.messageBody = messageBody;
        }

        @Override
        public void run() {
            CreateProgressReport activity = activityReference.get();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog = ProgressDialog.show(activity, "Please wait", "Sending report...", true, false);
                    }
                });

                try {
                    Properties props = new Properties();
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.smtp.host", "smtp.gmail.com");
                    props.put("mail.smtp.port", "587");

                    Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(senderEmail, senderPassword);
                        }
                    });

                    MimeMessage mimeMessage = new MimeMessage(session);
                    mimeMessage.setFrom(new InternetAddress(senderEmail));
                    mimeMessage.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                    mimeMessage.addRecipients(javax.mail.Message.RecipientType.CC, InternetAddress.parse(guardianEmail));
                    mimeMessage.setSubject(subject);
                    mimeMessage.setText(messageBody);

                    Transport.send(mimeMessage);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity.getApplicationContext(), "Progress report sent!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity.getApplicationContext(), ReviewLearner.class);
                            activity.startActivity(intent);
                            activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                            activity.finish();
                        }
                    });
                } catch (MessagingException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity.getApplicationContext(), "Failed to send progress report", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        }
    }
}