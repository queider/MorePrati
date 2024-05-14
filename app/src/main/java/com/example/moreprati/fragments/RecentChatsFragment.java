package com.example.moreprati.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moreprati.R;
import com.example.moreprati.objects.RecentChat;
import com.example.moreprati.adapters.RecentChatsAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RecentChatsFragment extends Fragment {
    private DatabaseReference userRef;
    private RecyclerView recyclerView;
    private RecentChatsAdapter adapter;

    private String currentUserId;

    private BottomNavigationView bottomNavigationView;

    public RecentChatsFragment() {
        // Required empty public constructor
    }
    private DatabaseReference chatsRef;
    private DatabaseReference teachersRef;
    private DatabaseReference studentsRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_chats, container, false);
        Log.d("YAZAN", "RecentChatsFragment onCreate 1");

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CurrentUser", MODE_PRIVATE);
        currentUserId = sharedPreferences.getString("uid", "");
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize adapter
        adapter = new RecentChatsAdapter(recentChat -> startChatFragment(recentChat));

        // Set adapter to RecyclerView
        recyclerView.setAdapter(adapter);


        chatsRef = FirebaseDatabase.getInstance().getReference("Chats");
        teachersRef = FirebaseDatabase.getInstance().getReference("Teachers");
        studentsRef = FirebaseDatabase.getInstance().getReference("Students");

        if (currentUserId != null) {


            // Look through the "chats" node for nodes that include uid in their names
            chatsRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                    String chatName = dataSnapshot.getKey();
                    if (chatName.contains(currentUserId)) {
                        String otherUid = getOtherUid(currentUserId, chatName);
                        if (otherUid != null) {
                            // Make a "RecentChat" object from the otherUid
                            makeRecentChatFromUid(otherUid,chatName);
                        }
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                    // Handle child changes if needed
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    // Handle child removal if needed
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                    // Handle child movement if needed
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error if needed
                }
            });
        }


        recyclerView.setAdapter(adapter);

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        // Set the item selection listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            if (item.getItemId() == R.id.action_home) {
                fragment = new SearchFragment();
            }
            if (fragment != null) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                return true;
            }
            return false;
        });
        return view;
    }
    private String getOtherUid(String currentUid, String chatId) {
        // Extract the other user's uid from the chatId
        String[] uids = chatId.split("_");
        if (uids.length == 2) {
            return uids[0].equals(currentUid) ? uids[1] : uids[0];
        }
        return null;
    }private void makeRecentChatFromUid(String chatUserId, String chatName) {

        // Look through the "Teachers" node to find objects named after the otherUid
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("Chats").child(chatName).child("Messages");
        messagesRef.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String[] lastMessage = {""}; // Declare an array to store the last message

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    lastMessage[0] = snapshot.child("messageText").getValue(String.class);
                }

                final String lastMessageFinal = lastMessage[0]; // Final or effectively final variable
                // Look through the "Teachers" node to find objects named after the otherUid
                teachersRef.child(chatUserId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DataSnapshot teacherSnapshot = task.getResult();
                        if (teacherSnapshot != null && teacherSnapshot.exists()) {
                            String fullname = teacherSnapshot.child("fullname").getValue(String.class);
                            String imageUrl = teacherSnapshot.child("imageUrl").getValue(String.class);
                            String fcmToken = teacherSnapshot.child("fcmToken").getValue(String.class);

                            // Create RecentChat object from the teacher's data
                            RecentChat recentChat = new RecentChat(fullname, imageUrl, chatUserId ,chatName,fcmToken, lastMessageFinal);
                            adapter.addRecentChat(recentChat);
                        } else {
                            // If not found in "Teachers", look in "Students"
                            studentsRef.child(chatUserId).get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    DataSnapshot studentSnapshot = task1.getResult();
                                    if (studentSnapshot != null && studentSnapshot.exists()) {
                                        String fullname = studentSnapshot.child("fullname").getValue(String.class);
                                        String imageUrl = studentSnapshot.child("imageUrl").getValue(String.class);
                                        String fcmToken = studentSnapshot.child("fcmToken").getValue(String.class);
                                        // Create RecentChat object from the student's data
                                        RecentChat recentChat = new RecentChat(fullname, imageUrl, chatUserId ,chatName,fcmToken, lastMessageFinal);
                                        adapter.addRecentChat(recentChat);
                                    } else {
                                        // Handle case when neither teacher nor student is found
                                    }
                                } else {
                                    // Handle task failure if needed
                                }
                            });
                        }
                    } else {
                        // Handle task failure if needed
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error if needed
            }
        });
    }




    private void startChatFragment(RecentChat recentChat) {
        // Create a new instance of ChatFragment
        ChatFragment chatFragment = new ChatFragment();

        // Pass the RecentChats object as an argument to the fragment
        Bundle args = new Bundle();
        args.putSerializable("recentChat", recentChat);
        chatFragment.setArguments(args);

        // Replace the current fragment with ChatFragment
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, chatFragment)
                .addToBackStack(null)
                .commit();
    }
    private boolean isCurrentActivity(Class<?> activityClass) {
        return activityClass.isInstance(requireActivity());
    }
}
