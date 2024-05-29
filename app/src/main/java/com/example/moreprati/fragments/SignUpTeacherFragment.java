package com.example.moreprati.fragments;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.util.Log;
import android.widget.Toast;

import com.example.moreprati.R;
import com.example.moreprati.SubjectMapper;
import com.example.moreprati.activities.MainActivity;
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

import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class SignUpTeacherFragment extends Fragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance(); // firebase thing
    SubjectMapper subjectMapper = new SubjectMapper();
    private Map<String, Boolean> subjectMap;

    private Map<String, Boolean> CitySubjectMap;



    //profile pic
    private ImageView profilePic;
    private Button cameraButton;
    private String profilePicUri;
    private Uri profilePicLocalUri;


    //varablbes ----------
    private String fullname;
    private String email;
    private String password;
    private String city;

    private String[] subjectsArray = new String[0];

    private String wayOfLearningString;
    private int pricePerHour = 0;

    private String description;

    private String uid;
    private String fcmToken;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up_teacher, container, false);


        //camara -----------------------------------------------------------------------------------

        profilePic = view.findViewById(R.id.profilePic);
        cameraButton = view.findViewById(R.id.cameraButton);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImagePicker.with(SignUpTeacherFragment.this)
                        .crop(1f, 1f)                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        // Get info from signup ------------------------------------------------

        //auto complete cities menu
        Resources res = getResources();
        String[] cities = res.getStringArray(R.array.cites);
        AutoCompleteTextView cityMenu = view.findViewById(R.id.cityMenu);
        ArrayAdapter<String> adapterCity = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, cities);
        cityMenu.setAdapter(adapterCity);

        // subjects setups -------------------------------------------------------

        MultiAutoCompleteTextView subjectsMenu = view.findViewById(R.id.subjects);
        String[] subjectsMenuSuggestions = res.getStringArray(R.array.subjects);
        ArrayAdapter<String> adapterSubjectsMenu = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, subjectsMenuSuggestions);
        subjectsMenu.setAdapter(adapterSubjectsMenu);
        subjectsMenu.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());


        // Setup AutoCompleteTextView wayOfLearning --------------------------------------------------
        AutoCompleteTextView wayOfLearningMenu = view.findViewById(R.id.wayOfLearning);
        String[] wayOfLearningSuggestions = new String[]{"פרונטלי", "מרוחק", "מרוחק ופרונטלי"};
        ArrayAdapter<String> adapterWayOfLearning = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, wayOfLearningSuggestions);
        wayOfLearningMenu.setAdapter(adapterWayOfLearning);

        // Signup button ----------------------------------------------------------------------------
        Button SignupButton = view.findViewById(R.id.signUp);
        SignupButton.setOnClickListener(new View.OnClickListener() {
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


                String text = subjectsMenu.getText().toString();
                wayOfLearningString = wayOfLearningMenu.getText().toString();
                city = cityMenu.getText().toString();

                if (!text.isEmpty()) {
                    subjectsArray = processString(text);
                    subjectMap = SubjectMapper.mapSubjects(subjectsArray, requireContext());

                    // log the values of the map
                    for (Map.Entry<String, Boolean> entry : subjectMap.entrySet()) {
                        Log.d("YAZAN", "SubjectMap " + entry.getKey() + ": " + entry.getValue());

                    }

                    CitySubjectMap = SubjectMapper.mapCitySubjects(subjectsArray, city,requireContext());
                }


                if (validation()) {
                    registerTeacher();
                }
            }
        });

        return view;
    }



    private String[] processString(String input) {
        // Remove commas
        String stringWithoutCommas = input.replaceAll(",", "");

        // Split the string into an array using spaces
        String[] wordsArray = stringWithoutCommas.split("\\s+");

        return wordsArray;
    }



    // camara and profile pic -------------------------------------------------------------------------------------
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



    private void registerTeacher() {
        Log.d("YAZAN", "registerTeacher: " + email);
        Log.d("YAZAn", "registerTeacher: " + password);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithemail:success");
                            Toast.makeText(getContext(), "ההרשמה בוצעה בהצלחה", Toast.LENGTH_SHORT).show();
                            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            // Reauthenticate the user before uploading the profile picture
                            reauthenticateUser(email, password);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithemail:failure", task.getException());
                            Toast.makeText(getContext(), "ההרשמה כשלה / משתמש קיים", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    // Adding the student to the db -----------------------------------------------------------


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
        Teacher teacher = new Teacher(fullname, email, city, uid, subjectMap, CitySubjectMap,wayOfLearningString, pricePerHour, description, profilePicUri, fcmToken);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Teachers");
        usersRef.child(uid).setValue(teacher)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "linkUserToDatabase: Registers teacher");
                        registerTeacher(teacher);
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        Log.w(TAG, "linkUserToDatabase: Failure", task.getException());
                    }
                });
    }

    private void registerTeacher(Teacher teacher) {
        Log.d("YAZAN", "[+] User is Teacher -------------------------------- ");

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("isTeacher", true);
        editor.putString("fullname", teacher.getFullname());
        editor.putString("uid", teacher.getUid());
        editor.putString("imageUrl", teacher.getImageUrl());
        editor.putString("fcmToken", fcmToken);


        editor.apply();
    }

    private boolean validation() {

        if( fullname.isEmpty() || email.isEmpty() || city.isEmpty() || subjectMap.isEmpty() || wayOfLearningString.isEmpty() || pricePerHour == 0 || description.isEmpty() || subjectsArray[0] == null) {
            Toast.makeText(getContext(), "מלא את כל השדות", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fullname.length() > 15){
            Toast.makeText(getContext(), "שם מלא ארוך מידי", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(subjectsArray.length > 3) {
            Toast.makeText(getContext(), "ניתן לבחור עד 4 מ", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "אודות המורה ארוך מידי", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(description.length() < 20)
        {
            Toast.makeText(getContext(), "אודות קצר מידי", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(profilePicLocalUri == null) {
            Toast.makeText(getContext(), "העלה תמונת פרופיל", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    private void uploadProfilePicToStorage(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_pics/" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        profilePicUri =  uri.toString();
                        Log.d("uploadProfilePicToStorage: ", "File is uploaded, uri: " + profilePicUri);
                        linkUserToDatabase(); // Link user to the database after image upload success
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "uploadProfilePicToStorage: FAIL", e);
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
                            Log.d(TAG, "reauthenticateUser: success");
                            uploadProfilePicToStorage(profilePicLocalUri);
                        } else {
                            // Reauthentication failed
                            Log.w(TAG, "reauthenticateUser: failure", task.getException());

                        }
                    });
        }
    }


}