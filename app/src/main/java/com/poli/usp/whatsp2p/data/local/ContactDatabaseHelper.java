package com.poli.usp.whatsp2p.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.poli.usp.whatsp2p.Constants;

/**
 * Created by Bruno on 14-Aug-17.
 */

public class ContactDatabaseHelper {
    private SQLiteOpenHelper _openHelper;

    public ContactDatabaseHelper(Context context) {
        _openHelper = new BaseSQLiteOpenHelper(context);
    }

    public List<Contact> getContacts() {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }

        Cursor cursor =  db.rawQuery("select " +
                Constants.DB_CONTACT_FIELD_ID + ", " +
                Constants.DB_CONTACT_FIELD_NAME +  ", " +
                Constants.DB_CONTACT_FIELD_IP + ", " +
                Constants.DB_CONTACT_FIELD_PORT + ", " +
                Constants.DB_CONTACT_FIELD_SIGN_ENCODED_KEY + ", " +
                Constants.DB_CONTACT_FIELD_CHAT_ENCODED_KEY +
                " from " + Constants.DB_CONTACTS_TABLE ,  new String[] {});

        List<Contact> contacts = convertCursorToContacts(cursor);
        db.close();

        return contacts;
    }


    public long add(Contact contact) {
        try {
            SQLiteDatabase db = _openHelper.getWritableDatabase();
            if (db == null) {
                return 0;
            }
            ContentValues row = convertContactToContentValues(contact);
            long id = db.insert(Constants.DB_CONTACTS_TABLE, null, row);
            db.close();
            return id;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void update(Contact contact) {
        try {
            SQLiteDatabase db = _openHelper.getWritableDatabase();
            if (db == null) {
                return;
            }
            ContentValues row = convertContactToContentValues(contact);
            db.update(Constants.DB_CONTACTS_TABLE, row, "_id = ?", new String[]{contact.getId()});
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void delete(String id) {
        SQLiteDatabase db = _openHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        db.delete(Constants.DB_CONTACTS_TABLE, "_id = ?", new String[] { id });
        db.close();
    }

    public List<Contact> search(String username) {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }

        Cursor cursor =  db.rawQuery("select " +
                Constants.DB_CONTACT_FIELD_ID + ", " +
                Constants.DB_CONTACT_FIELD_NAME +  ", " +
                Constants.DB_CONTACT_FIELD_IP + ", " +
                Constants.DB_CONTACT_FIELD_PORT + ", " +
                Constants.DB_CONTACT_FIELD_SIGN_ENCODED_KEY + ", " +
                Constants.DB_CONTACT_FIELD_CHAT_ENCODED_KEY +
                " from " + Constants.DB_CONTACTS_TABLE +
                " where " + Constants.DB_CONTACT_FIELD_ID + " = '" + username + "'",  new String[] {});

        List<Contact> contacts = convertCursorToContacts(cursor);
        db.close();

        return contacts;
    }

    public boolean isFriendWithUsers(ArrayList<String> usersList) {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return false;
        }

        String users = "";
        Iterator<String> it = usersList.iterator();
        while (it.hasNext()) {
            if (!users.equals("")) {
                users += ",";
            }
            users += "'" + it.next() + "'";
        }

        Cursor cursor = db.rawQuery("select * " +
                "from "+ Constants.DB_CONTACTS_TABLE + " " +
                "where " + Constants.DB_CONTACT_FIELD_ID + " " +
                "in (" + users + ")", new String[] {});

        boolean isFriend = cursor.getCount() > 0;

        db.close();

        return isFriend;
    }

    private ContentValues convertContactToContentValues(Contact contact) throws IOException {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.DB_CONTACT_FIELD_ID, contact.getId());
        contentValues.put(Constants.DB_CONTACT_FIELD_NAME, contact.getName());
        contentValues.put(Constants.DB_CONTACT_FIELD_IP, contact.getIp());
        contentValues.put(Constants.DB_CONTACT_FIELD_PORT, contact.getPort());
        contentValues.put(Constants.DB_CONTACT_FIELD_SIGN_ENCODED_KEY, contact.getSignPublicKeyEncoded());
        contentValues.put(Constants.DB_CONTACT_FIELD_CHAT_ENCODED_KEY, contact.getChatPublicKeyRingEncoded());
        return contentValues;
    }

    private List<Contact> convertCursorToContacts(Cursor cursor){
        List<Contact> contacts = new ArrayList<>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            String name = cursor.getString(1);
            String ip = cursor.getString(2);
            int port = cursor.getInt(3);
            Contact contact = new Contact(id, name);
            contact.setIp(ip);
            contact.setPort(port);
            contact.setSignPublicKeyEncoded(cursor.getBlob(4));
            contact.setChatPublicKeyRingEncoded(cursor.getBlob(5));
            contacts.add(contact);
        }
        return contacts;
    }
}
