package com.example.moreprati;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //menu:

    private BottomNavigationView bottomNavigationView;


    // User Logging Things -------------------------
    private String uid;


    //firebase database things -----------------------------
    private DatabaseReference teachersRef;
    private TeacherAdapter teacherAdapter;
    private FirebaseAuth mAuth;

    //search menu things:-------------------------------------------------------

    String[] cites;
    String[] subjects = {"מתמטיקה", "עברית", "גיטרה", "אנגלית"};

    AutoCompleteTextView autoSubjectsMenu;
    AutoCompleteTextView autoCityMenu;


    ArrayAdapter<String> adapterSubjects;
    ArrayAdapter<String> adapterCity;


    String searchSubject = null;
    String searchCity = null;

    // rescyler thing:
    private static final int PAGE_SIZE = 10;
    private int lastVisibleItem = 0;


    // token


    private String fcmToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        teachersRef = FirebaseDatabase.getInstance().getReference("Teachers");




        //menu things ------------------------------------------------------------------------
        cites = getResources().getStringArray(R.array.cites);



        autoSubjectsMenu = findViewById(R.id.autoMenuSubjects);
        autoCityMenu = findViewById(R.id.autoCityMenu);



        adapterSubjects = new ArrayAdapter<String>(this, R.layout.item_list, subjects);
        adapterCity = new ArrayAdapter<String>(this, R.layout.item_list, cites);


        autoSubjectsMenu.setAdapter(adapterSubjects);
        autoCityMenu.setAdapter(adapterCity);




        autoSubjectsMenu.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                searchSubject = adapterView.getItemAtPosition(i).toString();
            }
        });

        autoCityMenu.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                searchCity= adapterView.getItemAtPosition(i).toString();
            }
        });

        // firebase things ------------------------------------------------------
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // User is authenticated (signed in)
            startActivity(new Intent(MainActivity.this, SignUp.class));

            //startActivity(new Intent(MainActivity.this, RecentChatsActivity.class));
        }
        //startActivity(new Intent(MainActivity.this, SignUp.class));

        Toast.makeText(this, "User is authenticated", Toast.LENGTH_SHORT).show();


        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        searchTeacherByUid(uid);

        getFCMToken();

        //if(fcmToken != "") teachersRef.child(uid).child("fcmToken").setValue(fcmToken);

        // recycler:
        setUpRecyclerView();

        Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpRecyclerView();
            }
        });


        Button clearButton = findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoCityMenu.setText("");

                autoSubjectsMenu.setText("");

                searchCity = null;

                searchSubject = null;

                setUpRecyclerView();
            }
        });

        // get logged in user information from database: --------------------------------------------


        //
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                 if (item.getItemId() == R.id.action_chat && !isCurrentActivity(RecentChatsActivity.class)) {
                    startActivity(new Intent(MainActivity.this, RecentChatsActivity.class));
                    Log.d("YAZAN", "[*] NAVBAR: pressed chat");
                    return true;
                }
                return false;
            }
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
        autoCityMenu.setText(teacher.getCity());
        Log.d("YAZAN", "[+] User is Teacher -------------------------------- ");

        SharedPreferences sharedPreferences = getSharedPreferences("CurrentUser", this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("isTeacher", true);
        editor.putString("teacherFullname", teacher.getFullname());
        editor.putString("teacherCity", teacher.getCity());
        editor.putString("teacherUID", teacher.getUid());
        editor.putString("teacherImage", teacher.getImage());
        editor.putString("fcmToken", fcmToken);

        editor.apply();
    }

    private void handleFoundStudent(Student student) {
        autoCityMenu.setText(student.getCity());
        Log.d("YAZAN", "[+] User is Student -------------------------------- ");

        SharedPreferences sharedPreferences = getSharedPreferences("CurrentUser", this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("isTeacher", false);
        editor.putString("studentFullname", student.getFullname());
        editor.putString("studentCity", student.getCity());
        editor.putString("studentUID", student.getUid());
        editor.putString("fcmToken", fcmToken);
        editor.apply();

    }





    private void setUpRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Query query = teachersRef;

        List<Query> conditions = new ArrayList<>();

        if (searchSubject != null) {
            conditions.add(query.orderByChild("subjects/" + searchSubject).equalTo(true));
        }

        if (searchCity != null) {
            conditions.add(query.orderByChild("city").equalTo(searchCity));
        }

        // Apply conditions
        for (Query condition : conditions) {
            query = condition;
        }

        Log.d("TAG", "[*] STARTED SEARCHING: " + "subject " + searchSubject + ", price " + searchCity + '.');


        FirebaseRecyclerOptions<Teacher> options =
                new FirebaseRecyclerOptions.Builder<Teacher>()
                        .setQuery(query, Teacher.class)
                        .build();

        teacherAdapter = new TeacherAdapter(options, new TeacherAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Teacher teacher) {
                // Handle item click, for example, open a new activity
                Intent intent = new Intent(MainActivity.this, TeacherInfo.class);
                intent.putExtra("fullname", teacher.getFullname());
                intent.putExtra("city", teacher.getCity());
                intent.putExtra("uid", teacher.getUid());
                intent.putExtra("subjects", (Serializable) teacher.getSubjects());
                intent.putExtra("wayOfLearning", teacher.getWayOfLearning());
                intent.putExtra("pricePerHour", teacher.getPricePerHour());
                intent.putExtra("description", teacher.getDescription());
                intent.putExtra("imageUrl", teacher.getImage());
                intent.putExtra("rating", teacher.getRating());
                intent.putExtra("fcmToken", teacher.getFcmToken());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(teacherAdapter);

        teacherAdapter.startListening();
    }



    @Override
    protected void onStart() {
        super.onStart();
        teacherAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (teacherAdapter != null) {
            teacherAdapter.stopListening();
        }
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