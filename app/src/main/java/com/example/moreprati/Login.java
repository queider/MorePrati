package com.example.moreprati;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();


        Button registerButton = findViewById(R.id.login);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = ((EditText) findViewById(R.id.mail)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();
                signInUser(mail, password);

            }
        });

        Button back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the function to navigate to the second activity
                goToSignUpActivity();

            }
        });

    }
    // nav to signup
    private void goToSignUpActivity() {
        // Create an Intent to start the SignUp activity
        Intent intent = new Intent(this, SignUp.class);

        // Start the SignUp activity
        startActivity(intent);
    }
    private void signInUser(String mail, String password) {

        mAuth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Toast.makeText(Login.this, "ההתחברות הצליחה, ברוכים הבאים", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, MainActivity.class));


                            // You can navigate to another activity or perform other actions here
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "ההתחברות נכשלה, פרטים לא נכונים", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}