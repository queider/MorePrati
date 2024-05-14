package com.example.moreprati.fragments;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moreprati.R;
import com.example.moreprati.SubjectMapper;
import com.example.moreprati.objects.Student;
import com.example.moreprati.objects.Student;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class StudentEditFragment extends Fragment {
    private AutoCompleteTextView cityMenu;
    private DatabaseReference studentReference;
    private String currentUserId;
    private String imageUrl;
    private String fullname;
    private String email;
    private String city;
    private boolean imageSetFlag = false;
    private Uri profilePicLocalUri;
    private String profilePicUri;
    private Button updateButton;
    private Button cameraButton;
    private ImageView profilePic;
    private TextView fullnameEditText;
    private TextView emailEditText;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_edit, container, false);
        cityMenu = view.findViewById(R.id.cityMenu);
        cameraButton = view.findViewById(R.id.cameraButton);
        updateButton = view.findViewById(R.id.updateButton);
        fullnameEditText = view.findViewById(R.id.fullname);
        emailEditText = view.findViewById(R.id.email);
        profilePic = view.findViewById(R.id.profilePic);

        //auto complete cities menu
        Resources res = getResources();
        String[] cities = res.getStringArray(R.array.cites);
        ArrayAdapter<String> adapterCity = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, cities);
        cityMenu.setAdapter(adapterCity);




        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getString("uid", "");
        studentReference = FirebaseDatabase.getInstance().getReference().child("Students").child(currentUserId);;


        studentReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the dataSnapshot exists and contains data
                if (dataSnapshot.exists()) {
                    // Retrieve the Student object from the dataSnapshot
                    Student student = dataSnapshot.getValue(Student.class);

                    imageUrl = student.getImageUrl();
                    city = student.getCity();
                    fullname = student.getFullname();
                    email = student.getEmail();

                    changeParamatersInView();

                } else {
                    // Handle the case where the Student data doesn't exist in the database
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that may occur
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImagePicker.with(StudentEditFragment.this)
                        .crop(1f, 1f)                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                city = cityMenu.getText().toString();

                if (!city.isEmpty()) {
                    Log.d("YAZAN", "[+] Edit Student: passed validation");
                    if (imageSetFlag) {
                        Log.d("YAZAN", "[*] Edit Student: new image detected, uploading..");
                        uploadProfilePicToStorage();;
                    } else {
                        Log.d("YAZAN", "[*] Edit Student: no image detected, continuing normal way");
                        updateDatabase();
                    }
                }
            }
        });
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == ImagePicker.REQUEST_CODE) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    imageSetFlag = true;
                    profilePic.setImageURI(uri);
                    profilePicLocalUri = uri;
                }
            }
        }
    }
    private void changeParamatersInView() {
        cityMenu.setText(city);
        fullnameEditText.setText(fullname);
        emailEditText.setText(email);
        Picasso.get().load(imageUrl).placeholder(R.drawable.default_profile_pic).into(profilePic);

    }

    private void updateDatabase() {
        Map<String, Object> updateValues = new HashMap<>();
        Log.d("YAZAN", "[*] Edit Student: updating database...");
        if(imageSetFlag){
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("imageUrl", profilePicUri);
            updateValues.put("imageUrl", profilePicUri);
        }
        updateValues.put("city", city);



        studentReference.updateChildren(updateValues)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "פרופיל עודכן", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "שגיאה בעדכון פרופיל", Toast.LENGTH_SHORT).show();

                    }
                });


    }
    private void uploadProfilePicToStorage() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_pics/" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(profilePicLocalUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        profilePicUri =  uri.toString();
                        Log.d("uploadProfilePicToStorage: ", "File is uploaded, uri: " + profilePicUri);
                        updateDatabase(); // Link user to the database after image upload success
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "uploadProfilePicToStorage: FAIL", e);
                    // Handle other failure cases or provide meaningful error messages
                });
    }


}