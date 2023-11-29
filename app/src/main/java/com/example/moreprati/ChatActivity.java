 package com.example.moreprati;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

// ChatActivity.java

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

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

import java.util.ArrayList;
import java.util.List;

 public class ChatActivity extends AppCompatActivity {

     private RecyclerView recyclerView;
     private TextInputLayout messageInputLayout;
     private EditText messageEditText;
     private Button sendButton;

     private FirebaseAuth firebaseAuth;
     private DatabaseReference messagesReference;
     private String currentUserId;
     private String chatUserId;

     private final List<Message> messagesList = new ArrayList<>();
     private MessageAdapter messageAdapter;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_chat);

         // Get user IDs from the intent

         currentUserId ="RLLQsDonF6dDUOUdyVWTiupfJNH3"; // should be the user uid
         chatUserId = "1RMAJtD8bfXhrM3oUNBqLXBqUB93"; // should be the chat user uid

         firebaseAuth = FirebaseAuth.getInstance();
         messagesReference = FirebaseDatabase.getInstance().getReference().child("Messages");

         recyclerView = findViewById(R.id.recyclerView);
         messageInputLayout = findViewById(R.id.messageEditText);
         messageEditText = messageInputLayout.findViewById(R.id.textInputEditText);

         sendButton = findViewById(R.id.sendButton);

         messageAdapter = new MessageAdapter(messagesList);
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

     private void loadMessages() {
         messagesReference.child(currentUserId).child(chatUserId)
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
             long timeStamp = System.currentTimeMillis(); // Adding the timestamp

             Message message = new Message(currentUserId, messageText, timeStamp);

             // Sending the message to both sender and receiver nodes
             messagesReference.child(currentUserId).child(chatUserId).push().setValue(message);
             messagesReference.child(chatUserId).child(currentUserId).push().setValue(message);

             messageEditText.setText("");
         }
     }
 }
