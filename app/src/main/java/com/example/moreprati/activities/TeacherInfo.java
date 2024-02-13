package com.example.moreprati.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.moreprati.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;
public class TeacherInfo extends AppCompatActivity {

    private String fullname;
    private String city;
    private String uid;
    private Map<String, Boolean> subjects;
    private String wayOfLearning;
    private int pricePerHour;
    private String description;
    private String imageUrl;
    private int rating;
    private String fcmToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_info);

        // Retrieve values from Intent
        Intent intent = getIntent();
        fullname = intent.getStringExtra("fullname");
        city = intent.getStringExtra("city");
        uid = intent.getStringExtra("uid");

        // Retrieve subjects using Serializable
        subjects = (Map<String, Boolean>) intent.getSerializableExtra("subjects");

        wayOfLearning = intent.getStringExtra("wayOfLearning");
        pricePerHour = intent.getIntExtra("pricePerHour", 0);
        description = intent.getStringExtra("description");
        imageUrl = intent.getStringExtra("imageUrl");
        rating = intent.getIntExtra("rating", 0);
        fcmToken = intent.getStringExtra("fcmToken");


        // Set values to views
        ImageView profileImageView = findViewById(R.id.profilePic);
        TextView fullnameTextView = findViewById(R.id.fullname);
        TextView ratingTextView = findViewById(R.id.rating);
        TextView wayOfLearningTextView = findViewById(R.id.wayOfLearning);
        TextView pricePerHourTextView = findViewById(R.id.pricePerHour);
        TextView cityTextView = findViewById(R.id.city);
        TextView subjectsTextView = findViewById(R.id.subjects);
        TextView descriptionEditText = findViewById(R.id.description);
        RatingBar ratingBar = findViewById(R.id.ratingBar);

        // Load image using Picasso
        Picasso.get().load(imageUrl).placeholder(R.drawable.default_profile_pic).into(profileImageView);

        // Set values to TextViews
        fullnameTextView.setText(fullname);
        ratingTextView.setText(String.valueOf(rating));
        wayOfLearningTextView.setText(wayOfLearning);
        pricePerHourTextView.setText(String.valueOf(pricePerHour));
        cityTextView.setText(city);

        // Extract subjects from the map and join them into a string
        StringBuilder subjectsStringBuilder = new StringBuilder();
        for (String subject : subjects.keySet()) {
            if (subjects.get(subject)) {
                if (subjectsStringBuilder.length() > 0) {
                    subjectsStringBuilder.append(", ");
                }
                subjectsStringBuilder.append(subject);
            }
        }
        subjectsTextView.setText(subjectsStringBuilder.toString());

        // Set description to EditText
        descriptionEditText.setText(description);


        Button makeContact = findViewById(R.id.contact);

        makeContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherInfo.this, ChatActivity.class);
                intent.putExtra("fullname", fullname);
                intent.putExtra("uid", uid);
                intent.putExtra("imageUrl", imageUrl);
                intent.putExtra("fcmToken", fcmToken);
                intent.putExtra("cameFromTeacherInfo", true);
                startActivity(intent);
            }
        });


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float userRating, boolean fromUser) {

// Assume you have a reference to your Firebase Database
                DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference().child("Teachers").child(uid);

// Step 1: Retrieve the current values
                teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Step 2: Modify the retrieved values
                        float currentRating = dataSnapshot.child("rating").getValue(Float.class);
                        int howManyRated = dataSnapshot.child("howManyRated").getValue(Integer.class);

                        float newRating = calculateNewRating(currentRating, howManyRated, userRating);
                        int newHowManyRated = howManyRated + 1;

                        ratingTextView.setText(String.valueOf(newRating));


                        // Step 3: Update the values in Firebase
                        teacherRef.child("rating").setValue(newRating);
                        teacherRef.child("howManyRated").setValue(newHowManyRated)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("FirebaseUpdate", "Values updated successfully!");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("FirebaseUpdate", "Error updating values: " + e.getMessage());
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("FirebaseUpdate", "Error reading values: " + databaseError.getMessage());
                    }
                });
                Log.d("Rating", "New Rating: " + rating);



            }
        });
    }

    private float calculateNewRating(float currentRating, int howManyRated, float userRating) {
        // Your logic to calculate the new rating based on the current rating,
        // number of ratings, and the user's new rating
        // For example, a simple average:
        float newRating = ((currentRating * howManyRated) + userRating) / (howManyRated + 1);

        // Round to one digit after the decimal point
        newRating = Math.round(newRating * 10.0f) / 10.0f;

        return newRating;
    }
}