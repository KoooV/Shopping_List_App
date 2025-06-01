package com.example.myapplication.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.example.myapplication.R;
import com.example.myapplication.db.DatabaseHelper;

public class RegisterActivity extends Activity {

    EditText editUsername, editPassword;
    Button btnRegister;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);

        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String username = editUsername.getText().toString().trim();
            String password = editPassword.getText().toString();

            if (username.isEmpty() || password.length() < 6) {
                Toast.makeText(this, getString(R.string.Invalid_username_or_password), Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.registerUser(username, password)) {
                Toast.makeText(this, getString(R.string.Registration_successful), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, getString(R.string.The_user_already_exists), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
