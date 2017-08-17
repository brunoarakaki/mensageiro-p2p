package br.com.mobile2you.m2ybase.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
                Constants.DB_CONTACT_FIELD_NAME + ", " +
            Constants.DB_CONTACT_FIELD_ID +  " from " +
            Constants.DB_CONTACTS_TABLE ,  new String[] {});
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

    public void delete(long id) {
        SQLiteDatabase db = _openHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        db.delete(Constants.DB_CONTACTS_TABLE, "_id = ?", new String[] { String.valueOf(id) });
        db.close();
    }

    private ContentValues convertContactToContentValues(Contact contact){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.DB_CONTACT_FIELD_ID, contact.getId());
        contentValues.put(Constants.DB_CONTACT_FIELD_NAME, contact.getName());
        return contentValues;
    }

    private List<Contact> convertCursorToContacts(Cursor cursor){
        List<Contact> contacts = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            contacts.add(new Contact(id, name));
        }
        return contacts;
    }
}
