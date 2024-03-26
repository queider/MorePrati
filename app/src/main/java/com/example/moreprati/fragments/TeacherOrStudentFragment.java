package com.example.moreprati.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.moreprati.R;

public class TeacherOrStudentFragment extends Fragment {

    public TeacherOrStudentFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_teacher_or_student, container, false);

        Button btnStudent = rootView.findViewById(R.id.btn_student);
        btnStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSignUpStudentFragment();
            }
        });

        Button btnLogin = rootView.findViewById(R.id.text_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLoginFragment();
            }
        });

        Button btnTeacher = rootView.findViewById(R.id.btn_teacher);
        btnTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchSignUpTeacherFragment();
            }
        });

        return rootView;
    }

    private void launchSignUpStudentFragment() {
        SignUpStudentFragment fragment = new SignUpStudentFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void launchLoginFragment() {
        LoginFragment fragment = new LoginFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
        private void launchSignUpTeacherFragment() {
            SignUpTeacherFragment fragment = new SignUpTeacherFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

    }
}
