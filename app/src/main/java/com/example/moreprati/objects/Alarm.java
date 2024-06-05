package com.example.moreprati.objects;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.moreprati.AlarmReceiver;
import com.example.moreprati.ObjectSerialization;

import java.util.Calendar;
import java.util.Random;

public class Alarm {
     private Calendar calendar;
     private String chatUserId;
     private String fullname;
     private int requestCode;

    public Alarm(Calendar calendar, String chatUserId, String fullname) {


        Random rand = new Random();

        this.calendar = calendar;
        this.chatUserId = chatUserId;
        this.fullname = fullname;
        this.requestCode = rand.nextInt(89999999) + 10000000;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public String getChatUserId() {
        return chatUserId;
    }

    public void setChatUserId(String chatUserId) {
        this.chatUserId = chatUserId;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public boolean setAlarm(Context context, Alarm alarm) {
        Log.d("YAZAN", "[*] Alarm: Setting alarm for: " + alarm.getFullname() + " with uid: " + alarm.getChatUserId());
        ObjectSerialization objectSerialization = new ObjectSerialization(context, chatUserId);
        if (objectSerialization.AlarmExist()){
            Log.d("YAZAN", "[+] Alarm: Existing alarm for: " + alarm.getFullname() + " with uid: " + alarm.getChatUserId());
            return false;
        }
        objectSerialization.saveAlarm(alarm);
        // Subtract 5 minutes from the selected time
        calendar.add(Calendar.MINUTE, -5);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("fullname", fullname); // Pass fullname to the receiver
        intent.putExtra("chatUserId", chatUserId); // Pass chatUserId to the receiver

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_MUTABLE);

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.d("YAZAN", "[+] Alarm: Made an alarm for: " + alarm.getFullname() + " with uid: " + alarm.getChatUserId());
            return true;
        }
        return false;
    }
}
