package com.example.moreprati;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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

        // Set rating to RatingBar
        ratingBar.setRating(rating);


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
    }
}
