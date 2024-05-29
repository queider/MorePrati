package com.example.moreprati;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
        Log.d("YAZAN", "[*] Alarm Received: for: " + fullname + " with uid: " + chatUserId);

        SharedPreferences sharedPreferences = context.getSharedPreferences("Alarms", Context.MODE_PRIVATE);

        // Check if the alarm is deleted
        if (sharedPreferences.contains(chatUserId)){

            // Send a broadcast to update ui for recived alarm/
            Intent broadcastIntent = new Intent("RECEIVED_ALARM");
            context.sendBroadcast(broadcastIntent);

            // Delete the chatUserId from shared preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(chatUserId);
            editor.apply();

            //Send notification
            showNotification(context, fullname);
            Log.d("YAZAN", "[*] Alarm Received: Notification sent for alarm regarding: " + fullname + " with uid: " + chatUserId);
        }
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
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("תזכורת לשיעור")
                .setContentText("יש לך שיעור בעוד 5 דקות עם " + fullname)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Notification notification = builder.build();

        // Show the notification
        notificationManager.notify(1, notification);



    }
}
