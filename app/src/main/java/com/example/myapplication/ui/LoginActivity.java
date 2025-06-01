package com.example.myapplication.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.example.myapplication.R;
import com.example.myapplication.db.DatabaseHelper;
import com.example.myapplication.model.User;

public class LoginActivity extends Activity {

    EditText editUsername, editPassword;
    Button btnLogin;
    DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);

        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String username = editUsername.getText().toString().trim();
            String password = editPassword.getText().toString();

            if (db.checkLogin(username, password)) {
                User.currentUser = username;
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, getString(R.string.Incorrect_credentials), Toast.LENGTH_SHORT).show();
            }
            SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("username", username);
            editor.apply();
            User.currentUser = username;

        });

        TextView textRegisterLink = findViewById(R.id.textRegisterLink);
        textRegisterLink.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

    }

}
