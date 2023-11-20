package com.example.test_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
// ... other imports ...

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        EditText emailEditText = findViewById(R.id.username);
        EditText passwordEditText = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login);
        loadSavedCredentials(emailEditText, passwordEditText);


        loginButton.setOnClickListener(view -> {
            try {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                signIn(email, password);
            }
            catch (Exception e){
                CustomToast.showShortToast(getApplicationContext(),"entered correct creds");

            }
        });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();
                        // Save the credentials to SharedPreferences
                        saveCredentials(email, password,uid);
                        CustomToast.showShortToast(getApplicationContext(), "success");
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                        // Authentication success, navigate to the main activity or another screen
                    } else {
                        CustomToast.showShortToast(getApplicationContext(),"Authentication failed");

                    }
                });
    }
    private void loadSavedCredentials(EditText emailEditText, EditText passwordEditText) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString("username", "");
        String savedPassword = sharedPreferences.getString("password", "");
        if (savedUsername.endsWith("com")) {
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
 }
        if (!savedUsername.isEmpty() && !savedPassword.isEmpty()) {
            // Set the saved username and password in the EditText fields
            emailEditText.setText(savedUsername);
            passwordEditText.setText(savedPassword);
        }
    }

    private void saveCredentials(String email, String password,String uid) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", email);
        editor.putString("password", password);
        editor.putString("uid", uid);
        editor.apply();
    }

}

