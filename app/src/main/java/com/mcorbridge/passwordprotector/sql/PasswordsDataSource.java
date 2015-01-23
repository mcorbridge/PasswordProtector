package com.mcorbridge.passwordprotector.sql;

/**
 * Created by Mike on 1/23/2015.
 * copyright Michael D. Corbridge
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class PasswordsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = { SQLiteHelper.COLUMN_ID,
                                    SQLiteHelper.COLUMN_ACTION,
                                    SQLiteHelper.COLUMN_CATEGORY,
                                    SQLiteHelper.COLUMN_MODIFIED,
                                    SQLiteHelper.COLUMN_NAME,
                                    SQLiteHelper.COLUMN_TITLE,
                                    SQLiteHelper.COLUMN_VALUE};

    public PasswordsDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Password createPassword(String action, String category, boolean modified, String name, String title, String value) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_ACTION, action);
        values.put(SQLiteHelper.COLUMN_CATEGORY, category);
        values.put(SQLiteHelper.COLUMN_MODIFIED, modified);
        values.put(SQLiteHelper.COLUMN_NAME, name);
        values.put(SQLiteHelper.COLUMN_TITLE, title);
        values.put(SQLiteHelper.COLUMN_VALUE, value);
        long insertId = database.insert(SQLiteHelper.TABLE_PASSWORDS, null, values);
        Cursor cursor = database.query(SQLiteHelper.TABLE_PASSWORDS, allColumns, SQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Password newPassword = cursorToPassword(cursor);
        cursor.close();
        return newPassword;
    }

    public void deletePassword(Password password) {
        long id = password.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(SQLiteHelper.TABLE_PASSWORDS, SQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Password> getAllPasswords() {
        List<Password> passwords = new ArrayList<Password>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_PASSWORDS,
                                        allColumns,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Password password = cursorToPassword(cursor);
            passwords.add(password);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return passwords;
    }

    private Password cursorToPassword(Cursor cursor) {
        Password password = new Password();
        password.setId(cursor.getLong(0));
        password.setAction(cursor.getString(1));
        password.setCategory(cursor.getString(2));
        password.setModified(true);
        password.setName(cursor.getString(4));
        password.setTitle(cursor.getString(5));
        password.setValue(cursor.getString(6));
        return password;
    }
}

