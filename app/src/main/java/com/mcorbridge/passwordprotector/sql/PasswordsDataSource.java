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

import com.mcorbridge.passwordprotector.constants.ApplicationConstants;

import java.util.ArrayList;
import java.util.List;

public class PasswordsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = { SQLiteHelper.COLUMN_ID,
                                    SQLiteHelper.COLUMN_PASSWORD_ID,
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

    public Password createPassword(Long id, String action, String category, int modified, String name, String title, String value) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_PASSWORD_ID, id);
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
        long pswdID = password.getPswdID();

        System.out.println("flag password as 'delete' with id: " + pswdID);
        //todo find a way to actually delete values that are flagged 'delete'
        //database.delete(SQLiteHelper.TABLE_PASSWORDS, SQLiteHelper.COLUMN_PASSWORD_ID + " = " + pswdID, null);

        // rather that delete the data, it will flagged as 'delete'
        String strFilter = SQLiteHelper.COLUMN_PASSWORD_ID +"=" + pswdID;
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_ACTION, ApplicationConstants.DELETE);
        database.update(SQLiteHelper.TABLE_PASSWORDS, values, strFilter, null);
    }

    public List<Password> getAllPasswords() {
        List<Password> passwords = new ArrayList<Password>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_PASSWORDS,
                                        allColumns,
                                        SQLiteHelper.COLUMN_ACTION + "!='delete'",
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

    public void setPasswordModifiedState(Password password, int state){
        ContentValues newValues = new ContentValues();
        newValues.put(SQLiteHelper.COLUMN_MODIFIED, state); // where state=0, data NOT a candidate for synchronization; state=1, data IS candidate for synchronization
        database.update(SQLiteHelper.TABLE_PASSWORDS, newValues, SQLiteHelper.COLUMN_PASSWORD_ID + "=" + password.getPswdID(), null);
    }

    public void updatePassword(Password password){
        String strFilter = SQLiteHelper.COLUMN_PASSWORD_ID +"=" + password.getPswdID();
        System.out.println(password.getPswdID() + " " + password.getAction() + " " + password.getCategory() + " " + password.isModified() + " " + password.getTitle() + " " + password.getValue());
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_ACTION, password.getAction());
        values.put(SQLiteHelper.COLUMN_CATEGORY, password.getCategory());
        values.put(SQLiteHelper.COLUMN_MODIFIED, password.isModified());
        values.put(SQLiteHelper.COLUMN_TITLE, password.getTitle());
        values.put(SQLiteHelper.COLUMN_VALUE, password.getValue());
        database.update(SQLiteHelper.TABLE_PASSWORDS, values, strFilter, null);
    }

    public void deleteLocalPasswordData(){
        database.delete(SQLiteHelper.TABLE_PASSWORDS, null, null);
    }

    private Password cursorToPassword(Cursor cursor) {
        Password password = new Password();
        password.setId(cursor.getInt(0));
        password.setPswdID(cursor.getLong(1));
        password.setAction(cursor.getString(2));
        password.setCategory(cursor.getString(3));
        password.setModified(cursor.getInt(4));
        password.setName(cursor.getString(5));
        password.setTitle(cursor.getString(6));
        password.setValue(cursor.getString(7));
        return password;
    }
}

