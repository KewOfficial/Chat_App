package com.example.chat_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView contactRecyclerView;
    private List<Contact> contactList;
    private ContactAdapter contactAdapter;
    private ChatDatabaseHelper dbHelper;
    private TextView usernameTextView;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactRecyclerView = findViewById(R.id.contactRecyclerView);
        Button logoutButton = findViewById(R.id.logoutButton);
        usernameTextView = findViewById(R.id.usernameTextView);

        dbHelper = new ChatDatabaseHelper(this);
        contactList = new ArrayList<>();
        contactAdapter = new ContactAdapter(contactList, new ContactAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Contact contact) {
                openChatRoom(contact);
            }
        });

        contactRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactRecyclerView.setAdapter(contactAdapter);

        SharedPreferences sharedPreferences = getSharedPreferences("chat_app_prefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "User");
        usernameTextView.setText(username);

        loadContacts();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void loadContacts() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        contactList.clear();

        Cursor cursor = db.query("Users", new String[]{"username"}, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                contactList.add(new Contact(username, true));
            }
            cursor.close();
            contactAdapter.notifyDataSetChanged();
        }
    }

    private void openChatRoom(Contact contact) {
        Intent intent = new Intent(MainActivity.this, ChatRoomActivity.class);
        intent.putExtra("loggedInUsername", username);
        intent.putExtra("contactUsername", contact.getUsername());
        startActivity(intent);
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("chat_app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("isLoggedIn");
        editor.remove("username");
        editor.apply();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
