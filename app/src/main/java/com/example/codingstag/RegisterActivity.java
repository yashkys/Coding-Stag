package com.example.codingstag;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.codingstag.databinding.ActivityRegisterBinding;
import com.example.codingstag.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnSignup;
    private TextView tvLogin;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseDatabase database;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        tvLogin = binding.textViewLogin;
        etEmail = binding.signupEmail;
        etPassword = binding.signupPassword;
        etConfirmPassword = binding.signupConfirmPassword;
        btnSignup = binding.signupButton;

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference().child("users");

        tvLogin.setOnClickListener(view1 -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        btnSignup.setOnClickListener(view1 -> {
            PerformAuth();
        });

    }

    private void PerformAuth() {
        String email = etEmail.getText().toString();
        String pass = etPassword.getText().toString();
        String confirmPass = etConfirmPassword.getText().toString();
        String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=. *[A-Z])(?=. *[ !@#&( )-[{}] : ; ', ?/ * ~$^+=<>]). {8,20}$";
        if(!email.matches(EMAIL_PATTERN)){
            etEmail.setError("Enter correct email");
        }
        else if(pass.length()<8 && !pass.matches(PASSWORD_PATTERN)){
            etPassword.setError("Password must contain at least : \n1. 8 characters \n2. One Uppercase letter \n3. One Lowercase letter \n4. A number \n5. One special character(@,$,_,etc)");
        }
        else if (!confirmPass.equals(pass)){
            etConfirmPassword.setError("Does not match");
        }else {
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Welcome User", Toast.LENGTH_SHORT).show();
                    mUser = mAuth.getCurrentUser();
                    assert mUser != null;
                    String uid = mUser.getUid();
                    // Create a User object with the retrieved information
                    User newUser = new User(email, uid);
                    // Store the user object in the Firebase Realtime Database
                    usersRef.child(uid).setValue(newUser);
                    sendToNextActivity();
                } else {
                    Toast.makeText(this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendToNextActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}