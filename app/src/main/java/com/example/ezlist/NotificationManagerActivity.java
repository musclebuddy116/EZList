package com.example.ezlist;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationManagerActivity extends AppCompatActivity { // Renamed to avoid conflict
    Button notifyBtn;

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        notifyBtn = findViewById(R.id.notify);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        notifyBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"MissingPermission", "NotificationPermission"})
            @Override
            public void onClick(View v) {
                Log.d("NotificationDebug", "Button clicked - trying to send notification");

                NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationManagerActivity.this, "My Notification")
                        .setContentTitle("My Title")
                        .setContentText("Notification is working")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(NotificationManagerActivity.this);
                managerCompat.notify(1, builder.build());
            }
        });

    }
}
