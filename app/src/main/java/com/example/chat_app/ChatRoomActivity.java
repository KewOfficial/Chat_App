package com.example.chat_app;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText messageEditText;
    private List<Message> messageList;
    private MessageAdapter messageAdapter;
    private ChatDatabaseHelper dbHelper;
    private String loggedInUsername;
    private String contactUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        ImageButton sendButton = findViewById(R.id.sendButton);

        loggedInUsername = getIntent().getStringExtra("loggedInUsername");
        contactUsername = getIntent().getStringExtra("contactUsername");

        dbHelper = new ChatDatabaseHelper(this);
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        loadMessages();
    }

    private void loadMessages() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        messageList.clear();

        Cursor cursor = db.query("Messages", new String[]{"username", "message", "timestamp"},
                "username = ? OR username = ?", new String[]{loggedInUsername, contactUsername},
                null, null, "timestamp ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
                messageList.add(new Message(username, message, timestamp));
            }
            cursor.close();
            messageAdapter.notifyDataSetChanged();
        }
    }

    private void sendMessage() {
        String message = messageEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(message)) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            long timestamp = System.currentTimeMillis();
            db.execSQL("INSERT INTO Messages (username, message, timestamp) VALUES (?, ?, ?)",
                    new Object[]{loggedInUsername, message, timestamp});
            messageList.add(new Message(loggedInUsername, message, String.valueOf(timestamp)));
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.smoothScrollToPosition(messageList.size() - 1);
            messageEditText.setText("");
        }
    }
}
