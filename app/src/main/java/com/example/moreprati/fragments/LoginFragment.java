package com.example.moreprati.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moreprati.R;
import com.example.moreprati.activities.MainActivity;
import com.example.moreprati.objects.Student;
import com.example.moreprati.objects.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;
    private String password;
    private String mail;

    private String fcmToken;
    private DatabaseReference teachersRef;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();
        teachersRef = FirebaseDatabase.getInstance().getReference("Teachers");

        Button loginButton = view.findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputLayout mailLayout = view.findViewById(R.id.mail);
                EditText mailEditText = mailLayout.getEditText();
                if (mailEditText != null) {
                    mail = mailEditText.getText().toString();
                }

                TextInputLayout passwordLayout = view.findViewById(R.id.password);
                EditText passwordEditText = passwordLayout.getEditText();
                if (passwordEditText != null) {
                    password = passwordEditText.getText().toString();
                }

                signInUser(mail, password);
            }
        });


        return view;
    }

    private void signInUser(String mail, String password) {
        mAuth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Toast.makeText(getActivity(), "ההתחברות הצליחה, ברוכים הבאים", Toast.LENGTH_SHORT).show();
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            getFCMToken();
                            searchTeacherByUid(uid);
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getActivity(), "ההתחברות נכשלה, פרטים לא נכונים", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void searchTeacherByUid(String uid) {
        teachersRef.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@android.support.annotation.NonNull DataSnapshot dataSnapshot) {
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
            public void onCancelled(@android.support.annotation.NonNull DatabaseError error) {
                Log.e("YAZAN", "Error searching for Teacher: " + error.getMessage());
            }
        });
    }

    private void searchStudentByUid(String uid) {
        DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("Students");

        studentsRef.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@android.support.annotation.NonNull DataSnapshot dataSnapshot) {
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
            public void onCancelled(@android.support.annotation.NonNull DatabaseError error) {
                Log.e("YAZAN", "[-] Error searching for student: " + error.getMessage());
            }
        });



    }

    private void handleFoundTeacher(Teacher teacher) {
        Log.d("YAZAN", "[+] User is Teacher -------------------------------- ");

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
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

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("isTeacher", false);
        editor.putString("fullname", student.getFullname());
        editor.putString("uid", student.getUid());
        editor.putString("image", student.getImage());
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
