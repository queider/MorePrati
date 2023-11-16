package com.example.moreprati;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference teachersRef;
    private TeacherAdapter teacherAdapter;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // User is authenticated (signed in)
            Toast.makeText(this, "User is authenticated", Toast.LENGTH_SHORT).show();

        } else {
            // User is not authenticated (signed out)
            startActivity(new Intent(MainActivity.this, SignUp.class));
        }

        // recycler:
        teachersRef = FirebaseDatabase.getInstance().getReference("teachers");

        setUpRecyclerView();

    }
    private void setUpRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // Query to get all teachers
        Query allTeachersQuery = teachersRef.orderByKey();
        FirebaseRecyclerOptions<Teacher> options =
                new FirebaseRecyclerOptions.Builder<Teacher>()
                        .setQuery(allTeachersQuery, Teacher.class)
                        .build();

        teacherAdapter = new TeacherAdapter(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(teacherAdapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        teacherAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        teacherAdapter.stopListening();
    }

}