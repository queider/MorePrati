package com.example.moreprati;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.moreprati.objects.Alarm;
import com.google.gson.Gson;

public class ObjectSerialization {

    private static final String PREF_NAME = "Alarms";
    private String AlarmChatUserId;

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public ObjectSerialization(Context context, String AlarmChatUserId) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        this.AlarmChatUserId = AlarmChatUserId;
    }

    public void saveAlarm(Alarm alarm) {
        String json = gson.toJson(alarm);
        sharedPreferences.edit().putString(AlarmChatUserId, json).apply();
    }

    public Alarm getAlarm() {
        String json = sharedPreferences.getString(AlarmChatUserId, null);
        if (json != null) {
            return gson.fromJson(json, Alarm.class);
        }
        return null;
    }
    public void removeAlarm(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(AlarmChatUserId);
        editor.apply();
    }

    public boolean AlarmExist() {
        return sharedPreferences.contains(AlarmChatUserId);
    }
}
