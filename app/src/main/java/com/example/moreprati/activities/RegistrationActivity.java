package com.example.moreprati.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.moreprati.R;
import com.example.moreprati.fragments.TeacherOrStudentFragment;

import com.example.moreprati.R;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        launchTeacherOrStudentFragment();
    }


    private void launchTeacherOrStudentFragment() {
        TeacherOrStudentFragment fragment = new TeacherOrStudentFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}