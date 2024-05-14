package com.example.moreprati.activities;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.moreprati.R;
import com.example.moreprati.adapters.TeacherAdapter;
import com.example.moreprati.fragments.RecentChatsFragment;
import com.example.moreprati.fragments.SearchFragment;
import com.example.moreprati.objects.Student;
import com.example.moreprati.objects.Teacher;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    //menu:

    private  BottomNavigationView bottomNavigationView;


    // User Logging Things -------------------------
    private String uid;


    //firebase database things -----------------------------
    private DatabaseReference teachersRef;



    //search menu things:-------------------------------------------------------

    private String fcmToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);



        // firebase things ------------------------------------------------------
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            // User is not authenticated
            startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            finish();
            return;
        }
        Toast.makeText(this, "User is authenticated", Toast.LENGTH_SHORT).show();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SearchFragment())
                    .commit();
        }



    }


}