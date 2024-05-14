package com.example.moreprati;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class FCMNofiticationService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessaging";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if the message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            // Handle data payload here
            String imageUrl = remoteMessage.getData().get("imageUrl"); // Get the imageUrl from the data payload

// Load the image using Picasso and set it as the large icon of the notification
            /*Picasso.get().load(imageUrl).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    // Create a notification builder
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                            .setSmallIcon(R.drawable.notification_icon)  // Use a placeholder icon for small icon
                            .setContentTitle("Notification Title")
                            .setContentText("Notification Text")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setLargeIcon(bitmap);  // Set the loaded bitmap as the large icon

                    // Show the notification
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                    notificationManager.notify(notificationId, builder.build());
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    e.printStackTrace();
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    // Handle image loading preparation
                }
            });


             */
        }

        // Check if the message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            // Handle notification payload here
        }
    }
}
