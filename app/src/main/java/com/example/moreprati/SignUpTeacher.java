package com.example.moreprati;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class SignUpTeacher extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance(); // firebase thing


    private MultiAutoCompleteTextView multiAutoCompleteTextView;
    private String[] subjects = new String[0];

    //profile pic
    ImageView profilePic;
    Button cameraButton;
    String profilePic64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_teacher);
        //camara
        profilePic = findViewById(R.id.profilePic);
        cameraButton = findViewById(R.id.cameraButton);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImagePicker.with(SignUpTeacher.this)
                        .crop(1f, 1f)	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        // Get info from signup ------------------------------------------------

        Intent intent = getIntent();
        String fullname = intent.getStringExtra("fullname");
        String mail = intent.getStringExtra("mail");
        String password = intent.getStringExtra("password");
        String city = intent.getStringExtra("city");


        // Displays the full name ------------------------------------------------------------------
        TextView fullnameDisplay = findViewById(R.id.fullname);
        fullnameDisplay.setText(fullname);

        // subjects setups -------------------------------------------------------

        multiAutoCompleteTextView = findViewById(R.id.subjects);
        String[] suggestions = new String[]{"מתמטיקה", "עברית", "אנגלית", "גיטרה", "פסנטר"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suggestions);
        multiAutoCompleteTextView.setAdapter(adapter);
        multiAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());


        // Setup AutoCompleteTextView wayOfLearning --------------------------------------------------
        AutoCompleteTextView wayOfLearning = findViewById(R.id.wayOfLearning);

        String[] suggestionsWayOfLearning = new String[]{"פרונטלי", "מרוחק", "מרוחק ופרונטלי"};
        ArrayAdapter<String> adapterWayOfLearning = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suggestionsWayOfLearning);

        wayOfLearning.setAdapter(adapterWayOfLearning);

        // Signup button ----------------------------------------------------------------------------
        Button SignupButton = findViewById(R.id.signUp);

        SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the selected values from the MultiAutoCompleteTextView
                String text = multiAutoCompleteTextView.getText().toString();
                String pricePerHour = ((EditText) findViewById(R.id.pricePerHour)).getText().toString();
                String description = ((EditText) findViewById(R.id.description)).getText().toString();
                String wayOfLearningString = wayOfLearning.getText().toString();

                if (!text.isEmpty()) {
                    // Add the selected value to the array
                    String[] newValues = new String[subjects.length + 1];
                    System.arraycopy(subjects, 0, newValues, 0, subjects.length);
                    newValues[subjects.length] = text;
                    subjects = newValues;
                    // Clear the MultiAutoCompleteTextView
                    multiAutoCompleteTextView.setText("");
                }
                if (validation(wayOfLearningString, pricePerHour, description, subjects, profilePic64)) {
                    registerTeacher(fullname, mail, password, city, subjects, wayOfLearningString, pricePerHour, description, profilePic64);
                }

            }
        });

        // back -----------------
        Button back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the function to navigate to the second activity
                goToSignUpActivity();

            }
        });

    }

    private void goToSignUpActivity() {
        // Create an Intent to start the SignUp activity
        Intent intent = new Intent(this, SignUp.class);

        // Start the SignUp activity
        startActivity(intent);
    }
    // camara and profile pic -------------------------------------------------------------------------------------


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        profilePic.setImageURI(uri);
        profilePic64 = uriToBase64(this, uri);

    }
    public static String uriToBase64(Context context, Uri imageUri) {
        try {
            // 1. Load the image data from the URI
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);

            if (inputStream == null) {
                return null;
            }

            // 2. Convert the image data to a byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // 3. Encode the byte array to a base64 string
            String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            return base64Image;
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Handle the exception
        }
    }


    private void registerTeacher(String fullname, String mail, String password, String city, String[] subjects, String wayOfLearningString , String pricePerHour, String description, String profilePic64) {


        mAuth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(SignUpTeacher.this, "ההרשמה בוצעה בהצלחה",
                                    Toast.LENGTH_SHORT).show();

                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();

                            // Now you can link the user to additional information in the database
                            linkUserToDatabase( fullname,  mail,  city,  uid , subjects,  wayOfLearningString , pricePerHour,  description, profilePic64);


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpTeacher.this, "ההרשמה כשלה / משתמש קיים",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    // Adding the student to the db -----------------------------------------------------------
    private void linkUserToDatabase(String fullname, String mail, String city, String uid, String[] subjects, String wayOfLearningString , String pricePerHour, String description, String profilePic64) {
        Teacher teacher = new Teacher( fullname,  mail,  city,  uid , Arrays.asList(subjects) /* firebase dosn't exept Arrays only Lists. */,  wayOfLearningString , Integer.parseInt(pricePerHour),  description,  profilePic64);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Teachers");
        String userKey = usersRef.push().getKey();
        usersRef.child(userKey).setValue(teacher);
        Log.d(TAG, "linkUserToDatabase: Registers teacher");
    }

    private boolean validation(String wayOfLearning, String pricePerHour, String description, String[] subjects, String profilePicBytes) {
        // checks if all fields are entered.
        if(wayOfLearning.isEmpty() || pricePerHour.isEmpty() || description.isEmpty() || subjects[0] == null) {
            Toast.makeText(this, "מלא את כל השדות", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            Integer.parseInt(pricePerHour);
        } catch(Exception e) {
            Toast.makeText(this, "מחיר לשעה שגוי", Toast.LENGTH_SHORT).show();
            return false;
        }

        // checks passport length
        if(pricePerHour.length() > 5) {
            Toast.makeText(this, "מחיר לשעה שגוי", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(description.length() > 400)
        {
            Toast.makeText(this, "אודות המורה קצר מידי", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(description.length() < 20)
        {
            Toast.makeText(this, "אודות ארוך מידי", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(profilePicBytes == null) {
            Toast.makeText(this, "העלה תמונת פרופיל", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}