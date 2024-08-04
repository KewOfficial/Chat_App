package com.example.chat_app;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ChatDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "chat_app.db";
    private static final int DATABASE_VERSION = 2;

    public ChatDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE Users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE, " +
                "password TEXT)";
        String CREATE_MESSAGES_TABLE = "CREATE TABLE Messages (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "message TEXT, " +
                "timestamp TEXT)";
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_MESSAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            if (!columnExists(db, "Messages", "timestamp")) {
                db.execSQL("ALTER TABLE Messages ADD COLUMN timestamp TEXT");
            }
            if (!columnExists(db, "Messages", "username")) {
                db.execSQL("ALTER TABLE Messages ADD COLUMN username TEXT");
            }
        }
    }

    private boolean columnExists(SQLiteDatabase db, String tableName, String columnName) {
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                if (name.equals(columnName)) {
                    cursor.close();
                    return true;
                }
            }
            cursor.close();
        }
        return false;
    }
}
