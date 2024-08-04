package com.example.chat_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView errorTextView;
    private ChatDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        errorTextView = findViewById(R.id.errorTextView);
        Button loginButton = findViewById(R.id.loginButton);
        Button registerRedirectButton = findViewById(R.id.registerRedirectButton);

        dbHelper = new ChatDatabaseHelper(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        registerRedirectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void loginUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            errorTextView.setText(R.string.username_password_empty_error);
            errorTextView.setVisibility(View.VISIBLE);
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = { "id" };
        String selection = "username = ? AND password = ?";
        String[] selectionArgs = { username, password };

        Cursor cursor = db.query("Users", columns, selection, selectionArgs, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            cursor.close();

            // Save login state in SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("chat_app_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", true);
            editor.putInt("userId", userId);
            editor.apply();

            // Redirect to Main Chat Interface
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            if (cursor != null) {
                cursor.close();
            }
            errorTextView.setText(R.string.incorrect_username_password_error);
            errorTextView.setVisibility(View.VISIBLE);
        }
    }
}
