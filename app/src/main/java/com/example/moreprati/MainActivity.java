package com.example.moreprati;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //menu:

    private BottomNavigationView bottomNavigationView;


    // User Logging Things -------------------------
    public Teacher teacherUserInfo;
    public Student studentUserInfo;

    public boolean ifTeacher;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        if (currentUser != null) {
            // User is authenticated (signed in)
            Toast.makeText(this, "User is authenticated", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(MainActivity.this, ChatActivity.class));


        } else {
            // User is not authenticated (signed out)
            startActivity(new Intent(MainActivity.this, SignUp.class));

        }


        // recycler:

        teachersRef = FirebaseDatabase.getInstance().getReference("Teachers");
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

        // get logged in user inforamtion from database: --------------------------------------------
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        searchTeacherByUid(uid);


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_home && !isCurrentActivity(MainActivity.class)) {
                    startActivity(new Intent(MainActivity.this, MainActivity.class));

                    Log.d("YAZAN", "[*] NAVBAR: pressed home");
                    return true;
                } else if (item.getItemId() == R.id.action_chat && !isCurrentActivity(Login.class)) {
                    startActivity(new Intent(MainActivity.this, Login.class));
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
                    Log.d("YAZAN", "Teacher with UID " + uid + " not found");
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
                            // Do something with the teacher object

                            // If you want to return the Teacher object, you can pass it to another function
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
        teacherUserInfo = teacher;
        ifTeacher = true;
        autoCityMenu.setText(teacherUserInfo.getCity());
        Log.d("YAZAN", "[+] User is Teacher -------------------------------- ");
    }

    private void handleFoundStudent(Student student) {
        studentUserInfo = student;
        ifTeacher = false;
        autoCityMenu.setText(studentUserInfo.getCity());
        Log.d("YAZAN", "[+] User is Student -------------------------------- ");
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

        Log.d("TAG", "[*] STARTED SEARCHING: " + "subject " + searchSubject + ", price "  + searchCity + '.');
        FirebaseRecyclerOptions<Teacher> options =
                new FirebaseRecyclerOptions.Builder<Teacher>()
                        .setQuery(query, Teacher.class)
                        .build();

        Log.d("TAG", "[+] SEARCHING ENDED    -----------------------------------------");

        teacherAdapter = new TeacherAdapter(options);

        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(teacherAdapter);

        // Start listening for changes
        teacherAdapter.startListening();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0) {
                    // End of the list reached, load more items
                    loadMoreItems();
                }
            }
        });


    }

    private void loadMoreItems() {
        // Update query to fetch the next set of items
        Query newQuery = teachersRef.orderByChild("yourOrderByField")
                .startAfter(lastVisibleItem)
                .limitToFirst(PAGE_SIZE);

        newQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Process the new data and update the lastVisibleItem
                // Add the new data to your adapter or dataset
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
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

}