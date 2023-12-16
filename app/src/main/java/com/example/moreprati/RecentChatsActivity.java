package com.example.moreprati;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class RecentChatsActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    private DatabaseReference userRef;
    private FirebaseRecyclerAdapter<RecentChats, RecentChatsAdapter.RecentChatsViewHolder> adapter;
    private RecyclerView recyclerView;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_chats);

        SharedPreferences sharedPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isTeacher", true)) {
            currentUserId = sharedPreferences.getString("teacherUID", "");
        } else {
            currentUserId = sharedPreferences.getString("StudentUID", "");
        }
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize userRef
        userRef = FirebaseDatabase.getInstance().getReference("Messages").child(currentUserId);

        // Initialize the adapter
        adapter = new RecentChatsAdapter(getOptions(), new RecentChatsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String chatUserId) {
                // Handle item click, for example, open a new activity
                Intent intentFromRC = new Intent(RecentChatsActivity.this, ChatActivity.class);
                intentFromRC.putExtra("chatUserIdFromRecentChats", chatUserId);
                Log.d("YAZAN", "onItemClick: chatUserId is " + chatUserId + " and is on intent");
                startActivity(intentFromRC);
            }

        });

        recyclerView.setAdapter(adapter);


        //
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_home && !isCurrentActivity(MainActivity.class)) {
                    startActivity(new Intent(RecentChatsActivity.this, MainActivity.class));

                    Log.d("YAZAN", "[*] NAVBAR: pressed home");
                    return true;
                }
                return false;
            }
        });
    }


    private boolean isCurrentActivity(Class<?> activityClass) {
        return activityClass.isInstance(RecentChatsActivity.this);
    }




    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private FirebaseRecyclerOptions<RecentChats> getOptions() {
        Query query = userRef;  // This is the base query without any specific "Info" node

        return new FirebaseRecyclerOptions.Builder<RecentChats>()
                .setQuery(query, RecentChats.class)
                .build();
    }
}
