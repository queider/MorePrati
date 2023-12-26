package com.example.moreprati;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.messaging.FirebaseMessaging;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    private DatabaseReference teachersRef;
    private TeacherAdapter teacherAdapter;
    private RecyclerView recyclerView;


    String[] cities;
    String[] subjects = {"מתמטיקה", "עברית", "גיטרה", "אנגלית"};

    AutoCompleteTextView autoSubjectsMenu;
    AutoCompleteTextView autoCityMenu;

    ArrayAdapter<String> adapterSubjects;
    ArrayAdapter<String> adapterCity;

    String searchSubject = null;
    String searchCity = null;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        teachersRef = FirebaseDatabase.getInstance().getReference("Teachers");
        recyclerView = view.findViewById(R.id.recyclerView);


        setUpRecyclerView();// <--- here it works fine

        //search menu
        cities = getResources().getStringArray(R.array.cites);

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

        // Set up the search and clear buttons
        Button searchButton = view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUpRecyclerView(); // <---- here is the problem
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

        return view;
    }

    private void setUpRecyclerView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        Query query = teachersRef;

        List<Query> conditions = new ArrayList<>();

        if (searchSubject != null) {
            conditions.add(query.orderByChild("subjects/" + searchSubject).equalTo(true));
        }

        if (searchCity != null) {
            conditions.add(query.orderByChild("city").equalTo(searchCity));
        }

        // Apply conditions
        for (Query condition : conditions) {
            query = condition;
        }

        Log.d("TAG", "[*] STARTED SEARCHING: " + "subject " + searchSubject + ", price " + searchCity + '.');


        FirebaseRecyclerOptions<Teacher> options =
                new FirebaseRecyclerOptions.Builder<Teacher>()
                        .setQuery(query, Teacher.class)
                        .build();

        teacherAdapter = new TeacherAdapter(options, teacher -> {
            // Handle item click, for example, open a new activity
            TeacherInfoFragment teacherInfoFragment = new TeacherInfoFragment();

            Bundle args = new Bundle();
            args.putString("fullname", teacher.getFullname());
            args.putString("city", teacher.getCity());
            args.putString("uid", teacher.getUid());
            args.putSerializable("subjects", (Serializable) teacher.getSubjects());
            args.putString("wayOfLearning", teacher.getWayOfLearning());
            args.putInt("pricePerHour", teacher.getPricePerHour());
            args.putString("description", teacher.getDescription());
            args.putString("imageUrl", teacher.getImage());
            args.putFloat("rating", teacher.getRating());
            args.putString("fcmToken", teacher.getFcmToken());

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

    @Override
    public void onStart() {
        super.onStart();
        if (teacherAdapter != null) {
            teacherAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (teacherAdapter != null) {
            teacherAdapter.stopListening();
        }
    }

}
