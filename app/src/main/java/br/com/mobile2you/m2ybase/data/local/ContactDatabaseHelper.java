package br.com.mobile2you.m2ybase.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import br.com.mobile2you.m2ybase.Constants;

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
        return convertCursorToContacts(cursor);
    }


    public long add(Contact contact) {
        SQLiteDatabase db = _openHelper.getWritableDatabase();
        if (db == null) {
            return 0;
        }
        ContentValues row = convertContactToContentValues(contact);
        long id = db.insert(Constants.DB_CONTACTS_TABLE, null, row);
        db.close();
        return id;
    }

    public void update(Contact contact) {
        SQLiteDatabase db = _openHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        ContentValues row = convertContactToContentValues(contact);
        db.update(Constants.DB_CONTACTS_TABLE, row, "_id = ?", new String[] { contact.getId() });
        db.close();
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
        return convertCursorToContacts(cursor);
    }

    private ContentValues convertContactToContentValues(Contact contact){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.DB_CONTACT_FIELD_ID, contact.getId());
        contentValues.put(Constants.DB_CONTACT_FIELD_NAME, contact.getName());
        contentValues.put(Constants.DB_CONTACT_FIELD_IP, contact.getIp());
        contentValues.put(Constants.DB_CONTACT_FIELD_PORT, contact.getPort());
        contentValues.put(Constants.DB_CONTACT_FIELD_SIGN_ENCODED_KEY, contact.getSignPublicKey().getEncoded());
        contentValues.put(Constants.DB_CONTACT_FIELD_CHAT_ENCODED_KEY, contact.getChatPublicKey().getEncoded());
        return contentValues;
    }

    private List<Contact> convertCursorToContacts(Cursor cursor){
        List<Contact> contacts = new ArrayList<>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(0);
            String name = cursor.getString(1);
            String ip = cursor.getString(2);
            int port = cursor.getInt(3);
            PublicKey signPublicKey = Utils.getPublicKeyFromEncoded("DSA", cursor.getBlob(4));
            PublicKey chatPublicKey = Utils.getPublicKeyFromEncoded("RSA", cursor.getBlob(5));
            Contact contact = new Contact(id, name);
            contact.setIp(ip);
            contact.setPort(port);
            contact.setSignPublicKey(signPublicKey);
            contact.setChatPublicKey(chatPublicKey);
            contacts.add(contact);
        }
        return contacts;
    }
}
