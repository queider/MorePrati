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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.moreprati.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class TeacherInfoFragment extends Fragment {

    private String fullname;
    private String city;
    private String uid;
    private Map<String, Boolean> subjects;
    private String wayOfLearning;
    private int pricePerHour;
    private int howManyRated;

    private String description;
    private String imageUrl;
    private float rating;
    private String fcmToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_info, container, false);


        Bundle args = getArguments();
        if (args != null) {
            fullname = args.getString("fullname");
            city = args.getString("city");
            uid = args.getString("uid");
            subjects = (Map<String, Boolean>) args.getSerializable("subjects");
            wayOfLearning = args.getString("wayOfLearning");
            pricePerHour = args.getInt("pricePerHour", 0);
            description = args.getString("description");
            imageUrl = args.getString("imageUrl");
            rating = args.getFloat("rating", 0);
            fcmToken = args.getString("fcmToken");
            howManyRated = args.getInt("howManyRated", 0);
        }

        RatingBar displayedRating = view.findViewById(R.id.displayedRating);
        ImageView profileImageView = view.findViewById(R.id.profilePic);
        TextView fullnameTextView = view.findViewById(R.id.fullname);
        TextView wayOfLearningTextView = view.findViewById(R.id.wayOfLearning);
        TextView pricePerHourTextView = view.findViewById(R.id.pricePerHour);
        TextView cityTextView = view.findViewById(R.id.city);
        TextView subjectsTextView = view.findViewById(R.id.subjects);
        TextView descriptionEditText = view.findViewById(R.id.description);
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        TextView howManyRatedTextView = view.findViewById(R.id.howManyRated);


        // Load image using Picasso
        Picasso.get().load(imageUrl).placeholder(R.drawable.default_profile_pic).into(profileImageView);

        // Set values to TextViews
        fullnameTextView.setText(fullname);
        displayedRating.setRating(rating);
        wayOfLearningTextView.setText(wayOfLearning);
        pricePerHourTextView.setText(String.valueOf(pricePerHour));
        cityTextView.setText(city);
        howManyRatedTextView.setText("(" + String.valueOf(howManyRated) + ")");

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

        Button makeContact = view.findViewById(R.id.contact);

        makeContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatFragment chatFragment = new ChatFragment();
                Bundle args = new Bundle();
                args.putString("fullname", fullname);
                args.putString("uid", uid);
                args.putString("imageUrl", imageUrl);
                args.putString("fcmToken", fcmToken);
                args.putBoolean("cameFromTeacherInfo", true);
                chatFragment.setArguments(args);

                // Replace current fragment with ChatFragment
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, chatFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
        boolean isTeacher = sharedPreferences.getBoolean("isTeacher", true);
        if (isTeacher) {
            MaterialCardView cardView = view.findViewById(R.id.ratingCard);
            cardView.setVisibility(View.GONE);
            makeContact.setVisibility(View.GONE);
        }
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float userRating, boolean fromUser) {
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("RatingPrefs", Context.MODE_PRIVATE);
                String lastRatingDate = sharedPreferences.getString("lastRatingDate", "");

                // Get the current date
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String currentDate = dateFormat.format(new Date());

                if (lastRatingDate.equals(currentDate)) {
                    // User already rated for the day, show toast
                    Toast.makeText(requireContext(), "תוכל לדרג פעם אחת ביום", Toast.LENGTH_SHORT).show();
                } else {
                    // Update the last rating date in SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("lastRatingDate", currentDate);
                    editor.apply();

                    DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference().child("Teachers").child(uid);
                    teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Step 2: Modify the retrieved values
                            float currentRating = dataSnapshot.child("rating").getValue(Float.class);
                            int howManyRated = dataSnapshot.child("howManyRated").getValue(Integer.class);

                            float newRating = calculateNewRating(currentRating, howManyRated, userRating);
                            int newHowManyRated = howManyRated + 1;

                            displayedRating.setRating(newRating);


                            // Step 3: Update the values in Firebase
                            teacherRef.child("rating").setValue(newRating);
                            teacherRef.child("howManyRated").setValue(newHowManyRated)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("FirebaseUpdate", "Values updated successfully!");
                                            Toast.makeText(requireContext(), "דורג!", Toast.LENGTH_SHORT).show();

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
            }
        });
        return view;
    }

    // Additional methods or overrides can be added as needed
    private float calculateNewRating(float currentRating, int howManyRated, float userRating) {
        // Define weights for the current rating and the user's new rating
        float currentWeight = (float) howManyRated / (howManyRated + 1);
        float userWeight = 1.0f / (howManyRated + 1);

        // Calculate the weighted average of the current rating and the user's new rating
        float newRating = (currentRating * currentWeight) + (userRating * userWeight);

        // Round to one digit after the decimal point
        newRating = Math.round(newRating * 10.0f) / 10.0f;

        return newRating;
    }

}
