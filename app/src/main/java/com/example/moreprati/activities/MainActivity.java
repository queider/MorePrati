package com.example.moreprati.activities;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.example.moreprati.R;
import com.example.moreprati.fragments.SearchFragment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        תפקיד הפונקציה הוא לבדוק האם קיים כבר משתמש במערכת
        אם קיים, הפונקצייה מעבירה אותו למסך הראשי
        אם לא, הפונקצייה מעבירה אותו למסך ההרשמה הראשוני
         */

        // בדיקה האם קיים משתמש באפליקציה
        FirebaseApp.initializeApp(this);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // אם לא קיים משתמש
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, RegistrationActivity.class)); // העבר למסך ההתחברות
            finish();
            return;
        }

        // אחרת, אם קיים משתמש
        Toast.makeText(this, "התחברות הצלחה", Toast.LENGTH_SHORT).show();
        if (savedInstanceState == null) { // בודק האם האקטיביטי נוצרה בפעם הראשונה מאז הכניסה לאפליקציה
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SearchFragment())
                    .commit();
        }
    }
}