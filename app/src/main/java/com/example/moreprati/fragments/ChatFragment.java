package com.example.moreprati.fragments;

import static android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM;

import static androidx.core.content.ContextCompat.registerReceiver;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

// ChatActivity.java

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moreprati.AlarmReceiver;
import com.example.moreprati.activities.MainActivity;
import com.example.moreprati.objects.Message;
import com.example.moreprati.R;
import com.example.moreprati.adapters.MessageAdapter;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class ChatFragment extends Fragment {


    private static final int REQUEST_CODE_NOTIFICATION = 123213;

    private RecyclerView recyclerView;
    private TextInputLayout messageInputLayout;
    private EditText messageEditText;
    private Button sendButton;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference messagesReference;
    private String currentUserId;
    private String chatUserId;

    private String fullname;
    private String imageUrl;

    private String ChatToken;
    private ImageView imageView;

    private TextView textView;

    private boolean isTeacher;


    private final List<Message> messagesList = new ArrayList<>();
    private MessageAdapter messageAdapter;

    //timer button
    private Button timerButton;
    private TextView selectedTimeTextView;

    private Button dateTimeButton;
    private TextView selectedDateTimeTextView;
    private Calendar calendar;
    SharedPreferences sharedPreferences ;
    private BroadcastReceiver receiver;

    private static final int REQUEST_CODE_VIBRATE = 200534; // Any unique integer value

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        timerButton = view.findViewById(R.id.timerButton);
        selectedDateTimeTextView = view.findViewById(R.id.selectedTimeTextView);


        //ui setup
        imageView = view.findViewById(R.id.image);
        textView = view.findViewById(R.id.fullname);
        //firebase:
        firebaseAuth = FirebaseAuth.getInstance();
        messagesReference = FirebaseDatabase.getInstance().getReference().child("Messages");

        // Get users info ------------------------------------------------------------------------------

        sharedPreferences = requireActivity().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getString("uid", "");

        Bundle args = getArguments();

        currentUserId = sharedPreferences.getString("uid", "");

        if (args != null && args.getBoolean("cameFromTeacherInfo", false)) {


            chatUserId = args.getString("uid");
            imageUrl = args.getString("imageUrl");
            fullname = args.getString("fullname");
            ChatToken = args.getString("fcmToken");
            isTeacher = args.getBoolean("isTeacher", true);
            textView.setText(fullname);
            Picasso.get().load(imageUrl).placeholder(R.drawable.default_profile_pic).into(imageView);


            //my info
            messagesReference.child(chatUserId).child(currentUserId).child("fullname").setValue(sharedPreferences.getString("fullname", ""));
            messagesReference.child(chatUserId).child(currentUserId).child("isTeacher").setValue(sharedPreferences.getBoolean("isTeacher", true));
            messagesReference.child(chatUserId).child(currentUserId).child("fcmToken").setValue(sharedPreferences.getString("fcmToken", ""));
            messagesReference.child(chatUserId).child(currentUserId).child("chatUserId").setValue(currentUserId);


            if (sharedPreferences.getBoolean("isTeacher", true)) {
                messagesReference.child(chatUserId).child(currentUserId).child("imageUrl").setValue(sharedPreferences.getString("image", ""));
            }
            //chat user info
            messagesReference.child(currentUserId).child(chatUserId).child("fullname").setValue(fullname);
            messagesReference.child(currentUserId).child(chatUserId).child("isTeacher").setValue(isTeacher);
            messagesReference.child(currentUserId).child(chatUserId).child("imageUrl").setValue(imageUrl);
            messagesReference.child(currentUserId).child(chatUserId).child("fcmToken").setValue(ChatToken);
            messagesReference.child(currentUserId).child(chatUserId).child("chatUserId").setValue(chatUserId);

        } else {
            Bundle argsFromRc = getArguments();
            chatUserId = argsFromRc.getString("chatUserIdFromRecentChats");

            Log.d("YAZAN ", "current user is (from RC): " + currentUserId);
            Log.d("YAZAN ", "chat user is: (from RC)" + chatUserId);

            messagesReference.child(currentUserId).child(chatUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    isTeacher = dataSnapshot.child("isTeacher").getValue(boolean.class);
                    if (isTeacher) {
                        imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    }
                    fullname = dataSnapshot.child("fullname").getValue(String.class);
                    ChatToken = dataSnapshot.child("fcmToken").getValue(String.class);
                    Log.d("YAZAN", "onDataChange: FullNAME IS " + fullname);
                    textView.setText(fullname);
                    Picasso.get().load(imageUrl).placeholder(R.drawable.default_profile_pic).into(imageView);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Log.d("YAZAN", "onDataChange: FullNAME IS 2" + fullname);
        }


        Log.d("YAZAN ChatAcitvity", "current user is: " + currentUserId);
        Log.d("YAZAN ChatAcitvity", "chat user is: " + chatUserId);
        // Setup ----------------------------------------------------------------------------------------
        recyclerView = view.findViewById(R.id.recyclerView);
        messageInputLayout = view.findViewById(R.id.messageEditText);
        messageEditText = messageInputLayout.findViewById(R.id.textInputEditText);

        sendButton = view.findViewById(R.id.sendButton);

        messageAdapter = new MessageAdapter(messagesList, currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(messageAdapter);
        loadMessages();
        // Alarm ----------------------------------------------------------------------------------


        sharedPreferences = requireContext().getSharedPreferences("Alarms", Context.MODE_PRIVATE);
        String savedDateTime = sharedPreferences.getString(chatUserId, null);
        if (sharedPreferences.contains(chatUserId)) {
            selectedDateTimeTextView.setText("נקבע שיעור ב "+ savedDateTime);
            timerButton.setBackgroundResource(R.drawable.baseline_alarm_off_24);
        }else {
            timerButton.setBackgroundResource(R.drawable.round_timer_24);

        }

        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!sharedPreferences.contains(chatUserId)) {
                    Log.d("YAZAN", "Alarm: No alarm is set, launching date picker.");
                    if (checkNotificationPermissions()) {
                        showDateTimePickerDialog();
                    }
                } else {
                    Log.d("YAZAN", "Alarm: Canceled Alarm");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(chatUserId);
                    editor.apply();
                    timerButton.setBackgroundResource(R.drawable.round_timer_24);
                    selectedDateTimeTextView.setText("לא נקבע שיעור");
                    Toast.makeText(requireContext(), "השיעור בוטל", Toast.LENGTH_SHORT).show();


                }
            }
        });

        // Register BroadcastReceiver to listen for broadcasts
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("YAZAN", "Alarm: alarm received, deleting from ui.");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(chatUserId);
                editor.apply();
                timerButton.setBackgroundResource(R.drawable.round_timer_24);
                selectedDateTimeTextView.setText("לא נקבע שיעור");
            }
        };
        IntentFilter filter = new IntentFilter("RECEIVED_ALARM");
        requireContext().registerReceiver(receiver, filter);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        // Enable the send button only if the message is not empty
        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                sendButton.setEnabled(!editable.toString().trim().isEmpty());
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the BroadcastReceiver when the Fragment's view is destroyed
        requireContext().unregisterReceiver(receiver);
    }

    private void loadMessages() {

        messagesReference.child(currentUserId).child(chatUserId).child("Messages")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        Message message = dataSnapshot.getValue(Message.class);
                        messagesList.add(message);
                        messageAdapter.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                        // Handle the case where a message is changed (if needed)
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        // Handle the case where a message is removed (if needed)
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                        // Handle the case where a message is moved (if needed)
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                    }
                });
    }


    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();

        if (!messageText.isEmpty()) {

            Message message = new Message(messageText, currentUserId, chatUserId);


            messagesReference.child(currentUserId).child(chatUserId).child("Messages").push().setValue(message);
            messagesReference.child(chatUserId).child(currentUserId).child("Messages").push().setValue(message);

            messagesReference.child(currentUserId).child(chatUserId).child("lastMessage").setValue(message.getMessageText());
            messagesReference.child(chatUserId).child(currentUserId).child("lastMessage").setValue(message.getMessageText());
            messageEditText.setText("");
            sendNotification(message.getMessageText());
        }
    }


    void sendNotification(String message) {

        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", fullname);
            notificationObj.put("body", message);

            JSONObject dataObj = new JSONObject();
            dataObj.put("userId", currentUserId);

            jsonObject.put("notification", notificationObj);
            jsonObject.put("data", dataObj);
            jsonObject.put("to", ChatToken);

            callApi(jsonObject);
            Log.d("YAZAN Nofitication", "sendNotification: fullname:" + fullname);
            Log.d("YAZAN Nofitication", "sendNotification: message:" + message);

            Log.d("YAZAN Nofitication", "sendNotification: currentUserId:" + currentUserId);
            Log.d("YAZAN Nofitication", "sendNotification: ChatToken:" + ChatToken);
        } catch (Exception e) {

        }
    }

    void callApi(JSONObject jsonObject) {
        MediaType JSON = MediaType.get("application/json");
        OkHttpClient client = new OkHttpClient();

        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization", "Bearer AAAAkODHfnA:APA91bFC8TTXP8mdtibNvf029SZOrPzXs2NBPdl8UVGuUIehrMdkpMWK6Ofg5aq4uYu_CSy9jCMQmxFSCPS8N9j8cZOQ2yRxHsfmcpLAJBEvJzpXR6hNZ4KrQTbGA1FOjvtg9Jxqk16E")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("YAZAN Nofitication", "nofitication failed");

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("YAZAN Nofitication", "Sent nofitication");

            }
        });
    }


    private void showDateTimePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_datetime_picker, null);
        builder.setView(dialogView);

        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true); // Set 24-hour format

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int dayOfMonth = datePicker.getDayOfMonth();
                int hourOfDay = timePicker.getHour();
                int minute = timePicker.getMinute();

                // Handle the selected date and time
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth, hourOfDay, minute);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String selectedDateTime = dateFormat.format(calendar.getTime());
                selectedDateTimeTextView.setText("נקבע שיעור ב "+ selectedDateTime);
                timerButton.setBackgroundResource(R.drawable.baseline_alarm_off_24);
                Toast.makeText(requireContext(), "נקבע שיעור! " , Toast.LENGTH_SHORT).show();


                // Check if an alarm is already set for this chatUserId
                sharedPreferences = requireContext().getSharedPreferences("Alarms", Context.MODE_PRIVATE);
                if (!sharedPreferences.contains(chatUserId)) {
                    // Save the alarm for this chatUserId
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(chatUserId, selectedDateTime);
                    editor.apply();

                    // Schedule the alarm
                    setAlarm(calendar, fullname);
                } else {
                    // Inform the user that an alarm is already set
                    Log.d("YAZAN", "setAlarm: alarm already set for the user");
                    Toast.makeText(requireContext(), "נקבע כבר שיעור, מחק את השיעור הנוכחי", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setAlarm(Calendar calendar, String fullname) {
        // Subtract 5 minutes from the selected time
        calendar.add(Calendar.MINUTE, -5);

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        intent.putExtra("fullname", fullname); // Pass fullname to the receiver
        intent.putExtra("chatUserId", chatUserId); // Pass chatUserId to the receiver

        int requestCode = 123; // Unique request code, you can define your own
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), requestCode, intent, PendingIntent.FLAG_MUTABLE);

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.d("YAZAN", "setAlarm: alarm set");

        }
    }


    public boolean checkNotificationPermissions() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        boolean areNotificationsEnabled = notificationManager.areNotificationsEnabled();

        if (areNotificationsEnabled) {
            Log.d("YAZAN", "Alarm: checkNotificationPermissions: " + areNotificationsEnabled);
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_NOTIFICATION);
            areNotificationsEnabled = notificationManager.areNotificationsEnabled();
            if (areNotificationsEnabled) {
                return true;
                } else {
                    return false;
                }
            }

    }
}