package com.example.moreprati;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Teacher> teacherList;

    // Constructor to receive the list of teachers
    public TeacherAdapter(List<Teacher> teacherList) {
        this.teacherList = teacherList;
    }
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
            startActivity(new Intent(MainActivity.this, SignUp.class));

        } else {
            // User is not authenticated (signed out)
            startActivity(new Intent(MainActivity.this, SignUp.class));
        }



    }
}