package com.example.moreprati;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.moreprati.objects.Alarm;
import com.example.moreprati.objects.ObjectSerialization;

import java.util.Map;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d("YAZAN", "Boot: Detected Boot");
            // Retrieve stored alarm objects from SharedPreferences

            SharedPreferences sharedPreferences = context.getSharedPreferences("Alarms", Context.MODE_PRIVATE);
            Map<String, ?> allEntries = sharedPreferences.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                String name = entry.getKey();
                Log.d("YAZAN", "Boot: Detected alarm with uid: " + name +", reviving it..");
                ObjectSerialization objectSerialization = new ObjectSerialization(context, name);
                Alarm alarm = objectSerialization.getAlarm();
                alarm.setAlarm(context, alarm);
            }

        }
    }
}
