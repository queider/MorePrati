package com.example.moreprati.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import com.example.moreprati.R;
import com.example.moreprati.fragments.TeacherOrStudentFragment;

public class RegistrationActivity extends AppCompatActivity {


    /*
    תפקיד האקטיביטי הוא להעביר את המשתמש למסך ההרשמה הראשוני ולארח את הפרגמנטים הקשורים בהרשמה
     */
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