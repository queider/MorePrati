package com.example.moreprati.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moreprati.activities.ChatActivity;
import com.example.moreprati.R;
import com.example.moreprati.objects.RecentChats;
import com.example.moreprati.adapters.RecentChatsAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class RecentChatsFragment extends Fragment {
    private DatabaseReference userRef;
    private FirebaseRecyclerAdapter<RecentChats, RecentChatsAdapter.RecentChatsViewHolder> adapter;
    private RecyclerView recyclerView;

    private String currentUserId;

    private BottomNavigationView bottomNavigationView;

    public RecentChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_chats, container, false);
        Log.d("YAZAN", "RecentChatsFragment onCreate 1");

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CurrentUser", MODE_PRIVATE);

        currentUserId = sharedPreferences.getString("uid", "");



        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize userRef
        userRef = FirebaseDatabase.getInstance().getReference("Messages").child(currentUserId);

        // Initialize the adapter        Log.d("YAZAN", "RecentChatsFragment onCreate 1");
        adapter = new RecentChatsAdapter(getOptions(), chatUserId -> {
            startChatActivity(chatUserId);
        });


        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }



    private FirebaseRecyclerOptions<RecentChats> getOptions() {
        Query query = userRef;  // This is the base query without any specific "Info" node

        return new FirebaseRecyclerOptions.Builder<RecentChats>()
                .setQuery(query, RecentChats.class)
                .build();
    }

    private void startChatActivity(String chatUserId){
        Intent intentFromRC = new Intent(requireContext(), ChatActivity.class);
        intentFromRC.putExtra("chatUserIdFromRecentChats", chatUserId);
        Log.d("YAZAN", "onItemClick: chatUserId is " + chatUserId + " and is on intent");
        startActivity(intentFromRC);
    }
    private boolean isCurrentActivity(Class<?> activityClass) {
        return activityClass.isInstance(requireActivity());
    }
}
