package com.example.moreprati.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moreprati.R;
import com.example.moreprati.activities.RegistrationActivity;
import com.example.moreprati.adapters.TeacherAdapter;
import com.example.moreprati.objects.Teacher;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.Serializable;

public class SearchFragment extends Fragment {

    String[] cities;
    String[] subjects;
    AutoCompleteTextView autoSubjectsMenu;
    AutoCompleteTextView autoCityMenu;
    ArrayAdapter<String> adapterSubjects;
    ArrayAdapter<String> adapterCity;
    String searchSubject = null;
    String searchCity = null;
    private DatabaseReference teachersRef;
    private TeacherAdapter teacherAdapter;
    private RecyclerView recyclerView;
    private ShapeableImageView profilePicImageView;
    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        teachersRef = FirebaseDatabase.getInstance().getReference("Teachers");
        recyclerView = view.findViewById(R.id.recyclerView);


        setUpRecyclerView();


        //loads profile pic image

         profilePicImageView = view.findViewById(R.id.profilePic);


        //search menu
        cities = getResources().getStringArray(R.array.cites);
        subjects = getResources().getStringArray(R.array.subjects);

        autoSubjectsMenu = view.findViewById(R.id.autoMenuSubjects);
        autoCityMenu = view.findViewById(R.id.autoCityMenu);

        adapterSubjects = new ArrayAdapter<>(requireContext(), R.layout.item_list, subjects);
        adapterCity = new ArrayAdapter<>(requireContext(), R.layout.item_list, cities);

        autoSubjectsMenu.setAdapter(adapterSubjects);
        autoCityMenu.setAdapter(adapterCity);

        autoSubjectsMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                searchSubject = adapterView.getItemAtPosition(i).toString();
            }
        });

        autoCityMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                searchCity = adapterView.getItemAtPosition(i).toString();
            }
        });

        // Set OnClickListener
        profilePicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
                boolean isTeacher = sharedPreferences.getBoolean("isTeacher", true);
                if (isTeacher) {
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new TeacherEditFragment())
                            .addToBackStack(null) // Add to back stack for back navigation
                            .commit();
                } else {

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new StudentEditFragment())
                            .addToBackStack(null) // Add to back stack for back navigation
                            .commit();
                }


            }
        });


        // Set up the search and clear buttons
        Button searchButton = view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (teacherAdapter != null) {
                    teacherAdapter.stopListening();
                    teacherAdapter = null;
                }
                setUpRecyclerView();
            }
        });

        Button clearButton = view.findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoCityMenu.setText("");
                autoSubjectsMenu.setText("");
                searchCity = null;
                searchSubject = null;
                setUpRecyclerView();
            }
        });


        Button logoutButton = view.findViewById(R.id.logout);


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
                builder.setTitle("הודעה");
                builder.setMessage("האם ברצונך לצאת מהמשתמש?");
                builder.setPositiveButton("צא", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
                        String currentUserId = sharedPreferences.getString("uid", "");
                        boolean isTeacher = sharedPreferences.getBoolean("isTeacher", true);
                        DatabaseReference userReference;
                        if (!isTeacher) {
                            userReference = FirebaseDatabase.getInstance().getReference().child("Students").child(currentUserId).child("fcmToken");
                        } else {
                            userReference = FirebaseDatabase.getInstance().getReference().child("Teachers").child(currentUserId).child("fcmToken");
                        }

                        userReference.setValue("")
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("YAZAN", "[+] Logging out: FCM token cleared successfully");
                                })
                                .addOnFailureListener(e -> {
                                    // Error occurred while setting the value
                                    Log.d("YAZAN", "[-] Logging out: FCM token clearance failed");
                                });

                        File sharedPreferencesDir = new File(requireContext().getApplicationInfo().dataDir + "/shared_prefs");

                        // Get a list of all files in the directory
                        File[] sharedPrefFiles = sharedPreferencesDir.listFiles();

                        // Iterate through each file and delete it
                        if (sharedPrefFiles != null) {
                            for (File sharedPrefFile : sharedPrefFiles) {
                                sharedPrefFile.delete();
                            }
                        }

                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mAuth.signOut();
                        startActivity(new Intent(requireActivity(), RegistrationActivity.class));
                        requireActivity().finish(); // Close the current activity
                    }
                });
                builder.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottom_navigation);

        // Set the item selection listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            if (item.getItemId() == R.id.action_home) {
                fragment = new SearchFragment();
            } else if (item.getItemId() == R.id.action_chat) {
                fragment = new RecentChatsFragment();
            }
            if (fragment != null) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
                return true;
            }
            return false;
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences CurrentUserSP = requireContext().getSharedPreferences("CurrentUser", Context.MODE_PRIVATE);
        String profilePicUri = CurrentUserSP.getString("imageUrl", "");
        if (!profilePicUri.isEmpty()) {
            Picasso.get()
                    .load(profilePicUri)
                    .placeholder(R.drawable.default_profile_pic) // Placeholder image while loading
                    .error(R.drawable.default_profile_pic) // Image to display if loading fails
                    .fit() // Fit the image into the ImageView
                    .centerCrop() // Center crop the image if it's not square
                    .into(profilePicImageView);

        }

    }

    private void setUpRecyclerView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        Query query = teachersRef;


        if (searchSubject != null && searchCity == null) {
            query = query.orderByChild("subjects/" + searchSubject).equalTo(true);
            Log.d("Yazan", "[*] 1 Search: searching for " + searchSubject + " & null city");
        }

        if (searchSubject == null && searchCity != null) {
            query = query.orderByChild("city").equalTo(searchCity);
            Log.d("Yazan", "[*] 2 Search: searching for null subject & " + searchCity);
        }

        if (searchSubject != null && searchCity != null) {
            query = query.orderByChild("citySubjects/" + searchCity + "_" + searchSubject).equalTo(true);
            Log.d("Yazan", "[*] 3  Search: searching for " + searchCity + "_" + searchSubject);
        }

        if (searchSubject == null && searchCity == null) {
            Log.d("Yazan", "[*] 4 Search: not searching");
        }

        // Apply conditions

        Log.d("TAG", "[*] STARTED SEARCHING: " + "subject " + searchSubject + ", city " + searchCity + '.');


        FirebaseRecyclerOptions<Teacher> options =
                new FirebaseRecyclerOptions.Builder<Teacher>()
                        .setQuery(query, Teacher.class)
                        .build();

        teacherAdapter = new TeacherAdapter(options, teacher -> {
            // Handle item click, for example, open a new activity
            TeacherInfoFragment teacherInfoFragment = new TeacherInfoFragment();
            Bundle args = new Bundle();
            args.putString("uid", teacher.getUid());
            args.putSerializable("subjects", (Serializable) teacher.getSubjects());
            args.putString("wayOfLearning", teacher.getWayOfLearning());
            args.putInt("pricePerHour", teacher.getPricePerHour());
            args.putString("description", teacher.getDescription());
            args.putString("imageUrl", teacher.getImageUrl());
            args.putFloat("rating", teacher.getRating());
            args.putString("fcmToken", teacher.getFcmToken());
            args.putInt("howManyRated", teacher.getHowManyRated());


            teacherInfoFragment.setArguments(args);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, teacherInfoFragment)
                    .addToBackStack(null) // Add to back stack for back navigation
                    .commit();
        });

        recyclerView.setAdapter(teacherAdapter);
        teacherAdapter.startListening();
    }
}
