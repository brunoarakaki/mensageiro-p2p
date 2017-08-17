package br.com.mobile2you.m2ybase.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.com.mobile2you.m2ybase.Constants;

/**
 * Created by Bruno on 15-Aug-17.
 */

public class BaseSQLiteOpenHelper extends SQLiteOpenHelper {
    BaseSQLiteOpenHelper(Context context) {
        super(context, "main.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + Constants.DB_CONTACTS_TABLE + " (" +
                Constants.DB_CONTACT_FIELD_ID + " integer primary key autoincrement, " +
                Constants.DB_CONTACT_FIELD_NAME + " text)");
        db.execSQL("create table " + Constants.DB_MESSAGES_TABLE + " (" +
                Constants.DB_MESSAGES_FIELD_MESSAGE_ID + " integer primary key autoincrement, " +
                Constants.DB_MESSAGES_FIELD_SENDER_ID + " integer key, " +
                Constants.DB_MESSAGES_FIELD_RECEIVER_ID + " integer key, " +
                Constants.DB_MESSAGES_FIELD_TEXT + " text, " +
                Constants.DB_MESSAGES_FIELD_SENT_AT + " DATETIME)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}