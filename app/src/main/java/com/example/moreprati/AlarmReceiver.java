package com.example.moreprati;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.moreprati.R;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String fullname = intent.getStringExtra("fullname");
        String chatUserId = intent.getStringExtra("chatUserId");

        // Handle the alarm action here

        // Delete the chatUserId from shared preferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("Alarms", Context.MODE_PRIVATE);
        if (sharedPreferences.contains(chatUserId)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(chatUserId);
            editor.apply();
        }
        showNotification(context, fullname);
    }

    private void showNotification(Context context, String fullname) {
        String CHANNEL_ID = "lesson_notification_channel";
        String CHANNEL_NAME = "Lesson Notifications";

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_school_24)
                .setContentTitle("Lesson Reminder")
                .setContentText("You have a lesson with " + fullname)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Notification notification = builder.build();

        // Show the notification
        notificationManager.notify(1, notification);

        Log.d("Yazan", "showNotification:nofitication sent");

    }
}
