package br.com.mobile2you.m2ybase.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import br.com.mobile2you.m2ybase.data.remote.models.MessageResponse;

/**
 * Created by Bruno on 13-Aug-17.
 */

/**
 * Class that wraps the most common database operations. This example assumes you want a single table and data entity
 * with two properties: a title and a priority as an integer. Modify in all relevant locations if you need other/more
 * properties for your data and/or additional tables.
 */
public class MessageDatabaseHelper {
    private static final String MESSAGES_TABLE = "messages";
    private static final String MESSAGE_ID_FIELD = "_id";
    private static final String SENDER_ID_FIELD = "sender_id";
    private static final String RECEIVER_ID_FIELD = "receiver_id";
    private static final String TEXT_FIELD = "text";
    private static final String SENT_AT_FIELD = "sent_at";
    private SQLiteOpenHelper _openHelper;

    /**
     * Construct a new database helper object
     * @param context The current context for the application or activity
     */
    public MessageDatabaseHelper(Context context) {
        _openHelper = new MessageSQLiteOpenHelper(context);
    }

    /**
     * This is an internal class that handles the creation of all database tables
     */
    class MessageSQLiteOpenHelper extends SQLiteOpenHelper {
        MessageSQLiteOpenHelper(Context context) {
            super(context, "main.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + MESSAGES_TABLE + " (" +
                    MESSAGE_ID_FIELD + " integer primary key autoincrement, " +
                    SENDER_ID_FIELD + " integer key, " +
                    RECEIVER_ID_FIELD + " integer key, " +
                    TEXT_FIELD + " text, " +
                    SENT_AT_FIELD + " DATETIME)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    /**
     * Return a cursor object with all rows in the table.
     * @return A cursor suitable for use in a SimpleCursorAdapter
     */
    public List<MessageResponse> getMessagesFromContact(int user_id, int sender_id) {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        Cursor cursor =  db.rawQuery("select " + SENDER_ID_FIELD + ", " +
                RECEIVER_ID_FIELD + ", " +
                TEXT_FIELD + ", " +
                SENT_AT_FIELD + " from " +
                MESSAGES_TABLE + " where (" +
                SENDER_ID_FIELD + " = ? AND " +
                RECEIVER_ID_FIELD + " = ?) OR (" +
                SENDER_ID_FIELD + " = ? AND " +
                RECEIVER_ID_FIELD + " = ?)",  new String[] {
                String.valueOf(sender_id), String.valueOf(user_id),
                String.valueOf(user_id), String.valueOf(sender_id) });
        return convertCursorToMessages(cursor);
    }

    /**
     * Return values for a single row with the specified id
     * @param id The unique id for the row o fetch
     * @return All column values are stored as properties in the ContentValues object
     */
    public ContentValues get(long id) {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        ContentValues row = new ContentValues();
        Cursor cur = db.rawQuery("select title, priority from todos where _id = ?", new String[] { String.valueOf(id) });
        if (cur.moveToNext()) {
            row.put("title", cur.getString(0));
            row.put("priority", cur.getInt(1));
        }
        cur.close();
        db.close();
        return row;
    }

    public long add(MessageResponse message) {
        SQLiteDatabase db = _openHelper.getWritableDatabase();
        if (db == null) {
            return 0;
        }
        ContentValues row = convertMessageToContentValues(message);
        long id = db.insert(MESSAGES_TABLE, null, row);
        db.close();
        return id;
    }

    public void delete(long id) {
        SQLiteDatabase db = _openHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        db.delete("todos", "_id = ?", new String[] { String.valueOf(id) });
        db.close();
    }

    private ContentValues convertMessageToContentValues(MessageResponse message){
        ContentValues contentValues = new ContentValues();
        contentValues.put(SENDER_ID_FIELD, message.getSenderId());
        contentValues.put(RECEIVER_ID_FIELD, message.getReceiverId());
        contentValues.put(TEXT_FIELD, message.getText());
        contentValues.put(SENT_AT_FIELD, message.getSentAt().getTime());
        return contentValues;
    }

    private MessageResponse convertContentValuesToMessage(ContentValues contentValues){
        int senderId = contentValues.getAsInteger(SENDER_ID_FIELD);
        int receiverId = contentValues.getAsInteger(RECEIVER_ID_FIELD);
        String text = contentValues.getAsString(TEXT_FIELD);
        Timestamp sentAt = new Timestamp(contentValues.getAsLong(SENT_AT_FIELD));
        return new MessageResponse(senderId,  text,  receiverId,  sentAt);
    }

    private List<MessageResponse> convertCursorToMessages(Cursor cursor){
        List<MessageResponse> messages = new ArrayList<>();
        while (cursor.moveToNext()) {
            int senderId = cursor.getInt(0);
            int receiverId = cursor.getInt(1);
            String text = cursor.getString(2);
            Timestamp sentAt = new Timestamp(cursor.getLong(3));
            messages.add(new MessageResponse(senderId, text, receiverId,sentAt));
        }
        return messages;
    }
}
