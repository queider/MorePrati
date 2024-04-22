package com.example.moreprati.fragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;



public class SignUpStudentFragment extends Fragment {
    private FirebaseAuth mAuth; // firebase thing
    private String uid;
    private String fcmToken;
    private String fullname;
    private String mail;
    private String city;
    private String password;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_up_student, container, false);
        mAuth = FirebaseAuth.getInstance(); // firebase thing

        //auto complete cities menu
        Resources res = getResources();
        String[] cities = res.getStringArray(R.array.cites);
        AutoCompleteTextView cityMenu = rootView.findViewById(R.id.cityMenu);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, cities);
        cityMenu.setAdapter(adapter);

        // Register buttons
        Button registerButton = rootView.findViewById(R.id.studentSignUp);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 fullname = ((EditText) rootView.findViewById(R.id.fullname)).getText().toString();
                 mail = ((EditText) rootView.findViewById(R.id.mail)).getText().toString();
                 password = ((EditText) rootView.findViewById(R.id.password)).getText().toString();
                 city = cityMenu.getText().toString();

                if(validation()) {
                    registerStudent();
                }
            }
        });
        return rootView;
    }

    // Auth & Register in firebase
    private void registerStudent() {
        mAuth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SignUpFragment", "createUserWithEmail:success");
                            Toast.makeText(requireContext(), "ההרשמה בוצעה בהצלחה", Toast.LENGTH_SHORT).show();

                            uid = mAuth.getCurrentUser().getUid();

                            // Now you can link the user to additional information in the database
                            linkUserToDatabase();

                            startActivity(new Intent(requireContext(), MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SignUpFragment", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(requireContext(), "ההרשמה כשלה / משתמש קיים", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Adding the student to the db


    private void linkUserToDatabase() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fcmToken = task.getResult();
                Log.d("YAZAN", "getFCMToken (SignUPTeacher): " + fcmToken);
                proceedWithLinking();
            } else {
                Log.e("YAZAN", "Error getting FCM token: " + task.getException());
            }
        });
    }

    private void proceedWithLinking() {
        Student student = new Student(fullname, mail, city, uid, fcmToken);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Students");
        usersRef.child(uid).setValue(student)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "linkUserToDatabase: Registers student");
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        Log.w(TAG, "linkUserToDatabase: Failure", task.getException());
                    }
                });
    }
    // form validation
    private boolean validation() {
        // Add your validation logic here

        return true; // For now, always return true for simplicity
    }
}
