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
import android.widget.MultiAutoCompleteTextView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moreprati.R;
import com.example.moreprati.SubjectMapper;
import com.example.moreprati.objects.Teacher;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
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


public class TeacherEditFragment extends Fragment {
    private String city;

    private Map<String, Boolean> subjectsMap;
    private Map<String, Boolean> citySubjectsMap;

    private String wayOfLearning;

    private int pricePerHour;

    private String description;
    private String imageUrl;

    private String subjectsString;

    private float rating;
    private int howManyRated;
    private View view;

    private EditText descriptionEditText;
    private EditText pricePerHourEditText;
    private TextView howManyRatedEditText;
    private TextView fullnameTextView;
    private TextView emailTextView;

    private String email;
    private String fullname;



    private AutoCompleteTextView cityMenu;

    private MultiAutoCompleteTextView subjectsMenu ;

    private AutoCompleteTextView wayOfLearningMenu;
    private  RatingBar displayedRating;
    private Button updateButton;
    private String[] subjectsArray = new String[0];
    private ShapeableImageView profilePic;

    private String currentUserId;

    private DatabaseReference teacherReference;

    private Button cameraButton;
    private String profilePicUri;
    private Uri profilePicLocalUri;

    private boolean imageSetFlag = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_teacher_edit, container, false);


        // Get info from signup ------------------------------------------------

        TextInputLayout descriptionLayout = view.findViewById(R.id.description);
        descriptionEditText = descriptionLayout.getEditText();

        TextInputLayout pricePerHourLayout = view.findViewById(R.id.pricePerHour);
        pricePerHourEditText = pricePerHourLayout.getEditText();

        howManyRatedEditText = view.findViewById(R.id.howManyRated);
        fullnameTextView = view.findViewById(R.id.fullname);
        emailTextView = view.findViewById(R.id.email);

        displayedRating = view.findViewById(R.id.displayedRating);
        cityMenu = view.findViewById(R.id.cityMenu);
        subjectsMenu = view.findViewById(R.id.subjects);
        wayOfLearningMenu = view.findViewById(R.id.wayOfLearning);
        profilePic = view.findViewById(R.id.profilePic);
        cameraButton = view.findViewById(R.id.cameraButton);
        updateButton = view.findViewById(R.id.updateButton);

        Resources res = getResources();

        //auto complete cities menu
        String[] cities = res.getStringArray(R.array.cites);
        ArrayAdapter<String> adapterCity = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, cities);
        cityMenu.setAdapter(adapterCity);

        // subjects setups -------------------------------------------------------

        String[] subjectsMenuSuggestions = res.getStringArray(R.array.subjects);
        ArrayAdapter<String> adapterSubjectsMenu = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, subjectsMenuSuggestions);
        subjectsMenu.setAdapter(adapterSubjectsMenu);
        subjectsMenu.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());


        // Setup AutoCompleteTextView wayOfLearning --------------------------------------------------
        String[] wayOfLearningSuggestions = new String[]{"פרונטלי", "מרוחק", "מרוחק ופרונטלי"};
        ArrayAdapter<String> adapterWayOfLearning = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, wayOfLearningSuggestions);
        wayOfLearningMenu.setAdapter(adapterWayOfLearning);



        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getString("uid", "");
        teacherReference = FirebaseDatabase.getInstance().getReference().child("Teachers").child(currentUserId);;


        teacherReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the dataSnapshot exists and contains data
                if (dataSnapshot.exists()) {
                    // Retrieve the teacher object from the dataSnapshot
                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
                    fullname = teacher.getFullname();
                    imageUrl = teacher.getImageUrl();
                    city = teacher.getCity();
                    subjectsMap = teacher.getSubjects();
                    wayOfLearning = teacher.getWayOfLearning();
                    pricePerHour = teacher.getPricePerHour();
                    description = teacher.getDescription();
                    rating = teacher.getRating();
                    howManyRated = teacher.getHowManyRated();
                    fullname = teacher.getFullname();
                    email = teacher.getEmail();

                    changeParamatersInView();

                } else {
                    // Handle the case where the teacher data doesn't exist in the database
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

                ImagePicker.with(TeacherEditFragment.this)
                        .crop(1f, 1f)                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextInputLayout descriptionLayout = view.findViewById(R.id.description);
                EditText descriptionEditText = descriptionLayout.getEditText();
                if (descriptionEditText != null) {
                    description = descriptionEditText.getText().toString();
                }

                TextInputLayout pricePerHourLayout = view.findViewById(R.id.pricePerHour);
                EditText pricePerHourEditText = pricePerHourLayout.getEditText();
                if (pricePerHourEditText != null) {
                    try {
                        pricePerHour = Integer.parseInt(pricePerHourEditText.getText().toString());
                    } catch (NumberFormatException e) {
                        pricePerHour = 0;
                    }
                }


                subjectsString= subjectsMenu.getText().toString();
                wayOfLearning = wayOfLearningMenu.getText().toString();
                city = cityMenu.getText().toString();


                if (!subjectsString.isEmpty()) {
                    subjectsArray = processString(subjectsString);
                    subjectsMap = SubjectMapper.mapSubjects(subjectsArray, requireContext());

                    // log the values of the map
                    for (Map.Entry<String, Boolean> entry : subjectsMap.entrySet()) {
                        Log.d("YAZAN", "SubjectMap " + entry.getKey() + ": " + entry.getValue());

                    }
                    citySubjectsMap = SubjectMapper.mapCitySubjects(subjectsArray, city, requireContext());
                }

                if (validation()) {
                    Log.d("YAZAN", "[+] Edit Teacher: passed validation");
                    if (imageSetFlag) {
                        Log.d("YAZAN", "[*] Edit Teacher: new image detected, uploading..");
                        uploadProfilePicToStorage();;
                    } else {
                        Log.d("YAZAN", "[*] Edit Teacher: no image detected, continuing normal way");
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

    private void updateDatabase() {
// Create an update operation to set the new values of the object's properties
        Map<String, Object> updateValues = new HashMap<>();
        Log.d("YAZAN", "[*] Edit Teacher: updating database...");
        if(imageSetFlag){
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("imageUrl", profilePicUri);
            updateValues.put("imageUrl", profilePicUri);
            editor.apply();
        }
        updateValues.put("city", city);
        updateValues.put("citySubjects", citySubjectsMap);
        updateValues.put("subjects", subjectsMap);
        updateValues.put("wayOfLearning", wayOfLearning);
        updateValues.put("pricePerHour", pricePerHour);
        updateValues.put("description", description);



        teacherReference.updateChildren(updateValues)
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

    private void changeParamatersInView(){
        fullnameTextView.setText(fullname);
        emailTextView.setText(email);
        descriptionEditText.setText(description);
        pricePerHourEditText.setText(String.valueOf(pricePerHour));
        cityMenu.setText(city);
        wayOfLearningMenu.setText(wayOfLearning);
        subjectsMenu.setText(SubjectMapper.mapToString(subjectsMap));
        displayedRating.setRating(rating);
        howManyRatedEditText.setText("(" + String.valueOf(howManyRated) + ")");
        Picasso.get().load(imageUrl).placeholder(R.drawable.default_profile_pic).into(profilePic);

    }

    private String[] processString(String input) {
        // Remove commas
        String stringWithoutCommas = input.replaceAll(",", "");

        // Split the string into an array using spaces
        String[] wordsArray = stringWithoutCommas.split("\\s+");

        return wordsArray;
    }
    private boolean validation() {

        if(city.isEmpty() ||wayOfLearning.isEmpty() || pricePerHour == 0 || description.isEmpty() || subjectsArray[0] == null) {
            Toast.makeText(getContext(), "מלא את כל השדות", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(subjectsArray.length > 3) {
            Toast.makeText(getContext(), "ניתן לבחור עד 4 מ", Toast.LENGTH_SHORT).show();
            return false;
        }


        if(pricePerHour > 1000) {
            Toast.makeText(getContext(), "מחיר לשעה גדול מידי", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(pricePerHour < 10) {
            Toast.makeText(getContext(), "מחיר לשעה קטן מידי", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(description.length() > 400)
        {
            Toast.makeText(getContext(), "אודות המורה קצר מידי", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(description.length() < 20)
        {
            Toast.makeText(getContext(), "אודות ארוך מידי", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}