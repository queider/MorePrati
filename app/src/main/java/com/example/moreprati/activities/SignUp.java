package com.example.moreprati.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.moreprati.R;
import com.example.moreprati.objects.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth; // firebase thing
    private String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance(); // firebase thing

        //auto complite cites menu  --------------------------------------------------------------------------
        // Assuming you are in an activity or have access to a Context
        Resources res = getResources();

        // Retrieve the string array from XML
        String[] cites = res.getStringArray(R.array.cites);
        AutoCompleteTextView cityMenu = findViewById(R.id.cityMenu);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cites);

        cityMenu.setAdapter(adapter);


        //##Get info from ui/

        //register buttons--------------------------------------------------------------------------

        Button registerButton = findViewById(R.id.studentSignUp);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = ((EditText) findViewById(R.id.fullname)).getText().toString();
                String mail = ((EditText) findViewById(R.id.mail)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();
                String city = cityMenu.getText().toString();

                if(validation(fullname, mail, password, city))
                {
                    registerStudent(fullname, mail, password, city);
                }
            }
        });

        // Login Button --------------------------------------------------------------------------

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLogin();
            }
        });

        // Signup as a teacher button -------------------------------------------------------------

        Button loginAsATeacher = findViewById(R.id.loginAsATeacher);
        loginAsATeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullname = ((EditText) findViewById(R.id.fullname)).getText().toString();
                String mail = ((EditText) findViewById(R.id.mail)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();
                String city = cityMenu.getText().toString();

                if(validation(fullname, mail, password, city))
                {
                    goToSighUpTeacher(fullname, mail, password, city);
                }
            }
        });

    }


    private void goToSighUpTeacher(String fullname, String mail, String password, String city){

        Intent intent = new Intent(this, SignUpTeacher.class);
        intent.putExtra("fullname", fullname);
        intent.putExtra("mail", mail);
        intent.putExtra("password", password);
        intent.putExtra("city", city);
        startActivity(intent);
    }

    private void goToLogin() {
        // Create an Intent to start the SignUp activity
        Intent intent = new Intent(this, Login.class);

        // Start the SignUp activity
        startActivity(intent);
    }

    // Auth & Register in firebase ------------------------------------------------------------------
    private void registerStudent(String fullname, String mail, String password, String city) {


        mAuth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(SignUp.this, "ההרשמה בוצעה בהצלחה",
                                    Toast.LENGTH_SHORT).show();

                            uid = mAuth.getCurrentUser().getUid();

                            // Now you can link the user to additional information in the database
                            linkUserToDatabase(fullname, mail, city, uid);


                            startActivity(new Intent(SignUp.this, MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUp.this, "ההרשמה כשלה / משתמש קיים",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    // Adding the student to the db
    private void linkUserToDatabase(String fullname, String mail, String city, String uid) {
        Student student = new Student(fullname, mail, city, uid);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Students");
        String userKey = usersRef.push().getKey();
        usersRef.child(userKey).child(uid).setValue(student);
        Log.d(TAG, "linkUserToDatabase: DONEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
    }

    // form validation --------------------------------------------------------------------------
    private boolean validation(String fullname, String mail, String passport, String city) {


        /*
        // checks if all fields are entered.
        if(fullname.isEmpty() || mail.isEmpty() || passport.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "מלא את כל השדות", Toast.LENGTH_SHORT).show();
            return false;
        }
        // checks if the fullname is valid

        //      - checks if fullname is in hebrew
        if(!fullname.matches("^[\\u0590-\\u05FF\\s]+$")) {
            Toast.makeText(this, "הכנס שם מלא בעברית", Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, fullname, Toast.LENGTH_SHORT).show();
            return false;
        }
        //      - checks if fullname is שם פרטי ושם משפחה
        if(!fullname.contains(" ")){
            Toast.makeText(this, "הכנס שם פרטי ושם משפחה", Toast.LENGTH_SHORT).show();
            return false;
        }
        //      - checks if fullname is less from 20 chars.
        if(fullname.length() > 20){
            Toast.makeText(this, "שם מלא ארוך מידי", Toast.LENGTH_SHORT).show();
            return false;
        }
        // checks email
        if(!(mail != null && Patterns.EMAIL_ADDRESS.matcher(mail).matches())) {
            Toast.makeText(this, "אימייל שגוי", Toast.LENGTH_SHORT).show();
            return false;
        }
        // checks passport length
        if(passport.length() > 20) {
            Toast.makeText(this, "סיסמה ארוכה מידי", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(passport.length() < 5) {
            Toast.makeText(this, "סיסמה קצרה מידי", Toast.LENGTH_SHORT).show();
            return false;
        }
        */
        return true;
    }

}