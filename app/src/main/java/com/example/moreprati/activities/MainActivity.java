package com.example.moreprati.activities;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.moreprati.R;
import com.example.moreprati.adapters.TeacherAdapter;
import com.example.moreprati.fragments.AboutFragment;
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

    private BottomNavigationView bottomNavigationView;


    // User Logging Things -------------------------
    private String uid;


    //firebase database things -----------------------------
    private DatabaseReference teachersRef;
    private TeacherAdapter teacherAdapter;



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

        teachersRef = FirebaseDatabase.getInstance().getReference("Teachers");

        Toast.makeText(this, "User is authenticated", Toast.LENGTH_SHORT).show();

        Log.d("nigga", "crash");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        searchTeacherByUid(uid);
        SharedPreferences sharedPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        getFCMToken();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SearchFragment())
                    .commit();
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.action_home) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SearchFragment())
                        .commit();
                return true;
            } else if (item.getItemId() == R.id.action_chat) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new RecentChatsFragment())
                        .commit();
                return true;
            }else if (item.getItemId() == R.id.action_about) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AboutFragment())
                        .commit();
                return true;
            }
            return false;
        });



    }



    private boolean isCurrentActivity(Class<?> activityClass) {
        return activityClass.isInstance(MainActivity.this);
    }
//ON-CREATE-END//ON-CREATE-END//ON-CREATE-END//ON-CREATE-END//ON-CREATE-END//ON-CREATE-END//ON-CREATE-END//ON-CREATE-END//
//ON-CREATE-END//ON-CREATE-END//ON-CREATE-END//ON-CREATE-END//ON-CREATE-END//ON-CREATE-END//ON-CREATE-END//ON-CREATE-END//
//ON-CREATE-END//ON-CREATE-END//ON-CREATE-END//ON-CREATE-END//ON-CREATE-END//ON-CREATE-END//ON-CREATE-END//ON-CREATE-END//
    private void searchTeacherByUid(String uid) {
        teachersRef.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Teacher teacher = snapshot.getValue(Teacher.class);
                        // Handle the found Teacher object
                        if (teacher != null) {
                            Log.d("YAZAN", "Teacher found: " + teacher.getFullname());
                            // Do something with the teacher object

                            // If you want to return the Teacher object, you can pass it to another function
                            handleFoundTeacher(teacher);
                        }
                    }
                } else {
                    Log.d("YAZAN", "Teacher with UID " + uid + " not found, searching for student.... ");
                    searchStudentByUid(uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("YAZAN", "Error searching for Teacher: " + error.getMessage());
            }
        });
    }

    private void searchStudentByUid(String uid) {
        DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("Students");

        studentsRef.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Student student = snapshot.getValue(Student.class);
                        // Handle the found Teacher object
                        if (student != null) {
                            Log.d("YAZAN", "[+] Student found: " + student.getFullname());

                            handleFoundStudent(student);
                        }
                    }
                } else {
                    Log.d("YAZAN", "[-] Student with UID " + uid + " not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("YAZAN", "[-] Error searching for student: " + error.getMessage());
            }
        });



    }

    private void handleFoundTeacher(Teacher teacher) {
        Log.d("YAZAN", "[+] User is Teacher -------------------------------- ");

        SharedPreferences sharedPreferences = getSharedPreferences("CurrentUser", this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("isTeacher", true);
        editor.putString("fullname", teacher.getFullname());
        editor.putString("uid", teacher.getUid());
        editor.putString("image", teacher.getImage());
        editor.putString("fcmToken", fcmToken);

        editor.apply();
    }

    private void handleFoundStudent(Student student) {
        Log.d("YAZAN", "[+] User is Student -------------------------------- ");

        SharedPreferences sharedPreferences = getSharedPreferences("CurrentUser", this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("isTeacher", false);
        editor.putString("fullname", student.getFullname());
        editor.putString("uid", student.getUid());
        editor.putString("fcmToken", fcmToken);
        editor.apply();

    }

     void getFCMToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                fcmToken = task.getResult();
                Log.d("YAZAN", "getFCMToken: " + fcmToken);
            }
        });
    }

}