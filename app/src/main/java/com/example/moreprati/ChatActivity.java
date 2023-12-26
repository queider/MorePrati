 package com.example.moreprati;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

// ChatActivity.java

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64InputStream;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import android.util.Base64;

import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

 public class ChatActivity extends AppCompatActivity {



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

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_chat);
        //ui setup
         imageView = findViewById(R.id.image);
         textView = findViewById(R.id.fullname);
         //firebase:
         firebaseAuth = FirebaseAuth.getInstance();
         messagesReference = FirebaseDatabase.getInstance().getReference().child("Messages");

         // Get the users ids ---------------------------------------------------------------------
         //get my id:
         SharedPreferences sharedPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
         Intent intent = getIntent();


         currentUserId = sharedPreferences.getString("uid", "");

         if (intent.getBooleanExtra("cameFromTeacherInfo", false)) {


             chatUserId = intent.getStringExtra("uid");
             imageUrl = intent.getStringExtra("imageUrl");
             fullname = intent.getStringExtra(("fullname"));
             ChatToken = intent.getStringExtra(("fcmToken"));
             isTeacher = intent.getBooleanExtra("isTeacher", true);
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
             Intent intentFromRC = getIntent();
             chatUserId = intent.getStringExtra("chatUserIdFromRecentChats");

             Log.d("YAZAN ", "current user is (from RC): " + currentUserId);
             Log.d("YAZAN ", "chat user is: (from RC)" + chatUserId);

             messagesReference.child(currentUserId).child(chatUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     isTeacher = dataSnapshot.child("isTeacher").getValue(boolean.class);
                     if(isTeacher){
                         imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                     }
                     fullname = dataSnapshot.child("fullname").getValue(String.class);
                     ChatToken = dataSnapshot.child("fcmToken").getValue(String.class);
                     Log.d("Yazan", "onDataChange: FullNAME IS " + fullname);
                     textView.setText(fullname);
                     Picasso.get().load(imageUrl).placeholder(R.drawable.default_profile_pic).into(imageView);
                 }
                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             });
             Log.d("Yazan", "onDataChange: FullNAME IS 2" + fullname);
         }


         Log.d("YAZAN ChatAcitvity", "current user is: " + currentUserId);
         Log.d("YAZAN ChatAcitvity", "chat user is: " + chatUserId);


         recyclerView = findViewById(R.id.recyclerView);
         messageInputLayout = findViewById(R.id.messageEditText);
         messageEditText = messageInputLayout.findViewById(R.id.textInputEditText);

         sendButton = findViewById(R.id.sendButton);

         messageAdapter = new MessageAdapter(messagesList, currentUserId);
         recyclerView.setLayoutManager(new LinearLayoutManager(this));
         recyclerView.setAdapter(messageAdapter);

         loadMessages();

         sendButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 sendMessage();
             }
         });

         // Enable the send button only if the message is not empty
         messageEditText.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

             @Override
             public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

             @Override
             public void afterTextChanged(Editable editable) {
                 sendButton.setEnabled(!editable.toString().trim().isEmpty());
             }
         });


     }

     @Override
     public void onBackPressed() {
         super.onBackPressed();
         finish();
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


     void sendNotification(String message){

         try{
             JSONObject jsonObject  = new JSONObject();
             JSONObject notificationObj = new JSONObject();
             notificationObj.put("title",fullname);
             notificationObj.put("body",message);

             JSONObject dataObj = new JSONObject();
             dataObj.put("userId",currentUserId);

             jsonObject.put("notification",notificationObj);
             jsonObject.put("data",dataObj);
             jsonObject.put("to",ChatToken);

             callApi(jsonObject);
             Log.d("YAZAN Nofitication", "sendNotification: fullname:" + fullname);
             Log.d("YAZAN Nofitication", "sendNotification: message:" + message);

             Log.d("YAZAN Nofitication", "sendNotification: currentUserId:" + currentUserId);
             Log.d("YAZAN Nofitication", "sendNotification: ChatToken:" + ChatToken);
         }catch (Exception e){

         }
     }

     void callApi(JSONObject jsonObject){
         MediaType JSON = MediaType.get("application/json");
         OkHttpClient client = new OkHttpClient();

         String url = "https://fcm.googleapis.com/fcm/send";
         RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
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













 }
