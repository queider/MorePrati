package com.example.moreprati.fragments;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moreprati.R;
import com.example.moreprati.activities.MainActivity;
import com.example.moreprati.objects.Student;
import com.example.moreprati.objects.Teacher;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUpStudentFragment extends Fragment {
    private FirebaseAuth mAuth; // firebase thing
    private String uid;
    private String fcmToken;
    private String fullname;
    private String email;
    private String city;
    private String password;

    private ImageView profilePic;
    private Button cameraButton;

    private String profilePicUri;
    private Uri profilePicLocalUri;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up_student, container, false);
        mAuth = FirebaseAuth.getInstance(); // firebase thing

        profilePic = view.findViewById(R.id.profilePic);
        cameraButton = view.findViewById(R.id.cameraButton);



        //City menu setup:
        Resources res = getResources();
        String[] cities = res.getStringArray(R.array.cites);
        AutoCompleteTextView cityMenu = view.findViewById(R.id.cityMenu);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, cities);
        cityMenu.setAdapter(adapter);

        // Camara:
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImagePicker.with(SignUpStudentFragment.this)
                        .crop(1f, 1f)                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        // Register buttons
        Button registerButton = view.findViewById(R.id.studentSignUp);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputLayout fullnameLayout = view.findViewById(R.id.fullname);
                EditText fullnameEditText = fullnameLayout.getEditText();
                if (fullnameEditText != null) {
                    // Get the text from the EditText and convert it to string
                    fullname = fullnameEditText.getText().toString();
                }

                TextInputLayout emailLayout = view.findViewById(R.id.email);
                EditText emailEditText = emailLayout.getEditText();
                if (emailEditText != null) {
                    email = emailEditText.getText().toString();
                }

                TextInputLayout passwordLayout = view.findViewById(R.id.password);
                EditText passwordEditText = passwordLayout.getEditText();
                if (passwordEditText != null) {
                    password = passwordEditText.getText().toString();
                }

                city = cityMenu.getText().toString();

                Log.d("YAZAN", "[*] Student Registration: Clicked, Wating for validation.");
                if(validation()) {
                    Log.d("YAZAN", "[+] Student Registration: Validation passed, started registration for " + fullname + ".");
                    registerStudent();
                }else {
                    Log.d("YAZAN", "[*] Student Registration: Validation failed");
                }
            }
        });
        return view;
    }

    // Auth & Register in firebase
    private void registerStudent() {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("YAZAN", "[+] Student Registration: Registerd auth, doing database work now.");
                            Toast.makeText(requireContext(), "ההרשמה בוצעה בהצלחה", Toast.LENGTH_SHORT).show();

                            uid = mAuth.getCurrentUser().getUid();

                            // Now you can link the user to additional information in the database
                            reauthenticateUser(email, password);
                            startActivity(new Intent(requireContext(), MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("YAZAN", "[+] Student Registration: Failed auth.");
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
                Log.d("YAZAN", "[+] Student Registration: got fcm token: " + fcmToken);
                proceedWithLinking();
            } else {
                Log.d("YAZAN", "[-] Student Registration: fcm accumulation failed.");
            }
        });
    }

    private void proceedWithLinking() {
        Student student = new Student(fullname, email, city, uid, fcmToken, profilePicUri);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Students");
        usersRef.child(uid).setValue(student)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("YAZAN", "[+] Student Registration: created new student");
                        registerStudent(student);
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        Log.d("YAZAN", "[+] Student Registration: failed to create a student");
                    }
                });
    }
    private void registerStudent(Student student) {
        Log.d("YAZAN", "[+] User is Student -------------------------------- ");

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("isTeacher", false);
        editor.putString("fullname", student.getFullname());
        editor.putString("uid", student.getUid());
        editor.putString("imageUrl", student.getImageUrl());
        editor.putString("fcmToken", fcmToken);
        editor.apply();

    }

    private void uploadProfilePicToStorage(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_pics/" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        profilePicUri = uri.toString();
                        Log.d("YAZAN", "[+] Student Registration: pic uploaded. pic: " + profilePicUri);
                        linkUserToDatabase(); // Link user to the database after image upload success
                    });
                })
                .addOnFailureListener(e -> {
                    Log.d("YAZAN", "[-] Student Registration: pic upload failed.");
                    // Handle other failure cases or provide meaningful error messages
                });
    }

    private void reauthenticateUser(String email, String password) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // User has been reauthenticated successfully
                            Log.d("YAZAN", "[+] Student Registration: reauthenticateUser ok, uploading pic next.");
                            uploadProfilePicToStorage(profilePicLocalUri);
                        } else {
                            // Reauthentication failed
                            Log.d("YAZAN", "[-] Student Registration: reauthenticateUser failed.");

                        }
                    });
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == ImagePicker.REQUEST_CODE) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    profilePic.setImageURI(uri);
                    profilePicLocalUri = uri;
                }
            }
        }
    }

    // form validation
    private boolean validation() {

        if( fullname.isEmpty() || email.isEmpty() || city.isEmpty()) {
            Toast.makeText(getContext(), "מלא את כל השדות", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fullname.length() > 15){
            Toast.makeText(getContext(), "שם מלא ארוך מידי", Toast.LENGTH_SHORT).show();
            return false;
        }
        

        if (fullname.length() < 4){
            Toast.makeText(getContext(), "שם מלא קצר מידי", Toast.LENGTH_SHORT).show();
            return false;
        }

        // בדיקה של פורמט האיימיל
        final String email_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
        Pattern pattern = Pattern.compile(email_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if(!matcher.matches()){
            Toast.makeText(getContext(), "אימייל לא בפורמט הנכון", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (email.length() > 30){
            Toast.makeText(getContext(), "אימייל ארוך מידי", Toast.LENGTH_SHORT).show();
            return false;
        }
        

        if(profilePicLocalUri == null) {
            Toast.makeText(getContext(), "העלה תמונת פרופיל", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
