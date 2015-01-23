package com.mcorbridge.passwordprotector.sql;

/**
 * Created by Mike on 1/23/2015.
 * copyright Michael D. Corbridge
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_PASSWORDS = "passwords";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MODIFIED = "modified";
    public static final String COLUMN_ACTION = "action";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_VALUE = "value";
    private static final String DATABASE_NAME = "passwords.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_PASSWORDS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_MODIFIED + " boolean not null,"
            + COLUMN_ACTION + " text not null,"
            + COLUMN_CATEGORY + " text not null,"
            + COLUMN_NAME + " text not null,"
            + COLUMN_TITLE + " text not null,"
            + COLUMN_VALUE + " text not null"
            + ");";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println(SQLiteHelper.class.getName() + "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PASSWORDS);
        onCreate(db);
    }

}
