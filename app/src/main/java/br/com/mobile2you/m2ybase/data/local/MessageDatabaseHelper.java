package br.com.mobile2you.m2ybase.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import br.com.mobile2you.m2ybase.Constants;
import br.com.mobile2you.m2ybase.data.remote.models.MessageResponse;

/**
 * Created by Bruno on 13-Aug-17.
 */

public class MessageDatabaseHelper {
    private SQLiteOpenHelper _openHelper;

    public MessageDatabaseHelper(Context context) {
        _openHelper = new BaseSQLiteOpenHelper(context);
    }

    public List<MessageResponse> getMessagesFromContact(String user_id, String sender_id) {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        Cursor cursor =  db.rawQuery("select " + Constants.DB_MESSAGES_FIELD_SENDER_ID + ", " +
                Constants.DB_MESSAGES_FIELD_RECEIVER_ID + ", " +
                Constants.DB_MESSAGES_FIELD_SENDER_NAME + ", " +
                Constants.DB_MESSAGES_FIELD_RECEIVER_NAME + ", " +
                Constants.DB_MESSAGES_FIELD_TEXT + ", " +
                Constants.DB_MESSAGES_FIELD_SENT_AT + " from " +
                Constants.DB_MESSAGES_TABLE + " where (" +
                Constants.DB_MESSAGES_FIELD_SENDER_ID + " = ? AND " +
                Constants.DB_MESSAGES_FIELD_RECEIVER_ID + " = ?) OR (" +
                Constants.DB_MESSAGES_FIELD_SENDER_ID + " = ? AND " +
                Constants.DB_MESSAGES_FIELD_RECEIVER_ID + " = ?)",  new String[] {
                sender_id, user_id, user_id, sender_id });
        List<MessageResponse> messages = convertCursorToMessages(cursor);
        db.close();
        return messages;
    }



    public long add(MessageResponse message) {
        SQLiteDatabase db = _openHelper.getWritableDatabase();
        if (db == null) {
            return 0;
        }
        ContentValues row = convertMessageToContentValues(message);
        long id = db.insert(Constants.DB_MESSAGES_TABLE, null, row);
        db.close();
        return id;
    }

    public void delete(String id) {
        SQLiteDatabase db = _openHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        db.delete(Constants.DB_MESSAGES_TABLE, "_id = ?", new String[] { id });
        db.close();
    }

    public void deleteConversation(String user_id, String sender_id) {
        SQLiteDatabase db = _openHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        db.delete(Constants.DB_MESSAGES_TABLE, "(" +
                Constants.DB_MESSAGES_FIELD_SENDER_ID + " = ? AND " +
                Constants.DB_MESSAGES_FIELD_RECEIVER_ID + " = ?) OR (" +
                Constants.DB_MESSAGES_FIELD_SENDER_ID + " = ? AND " +
                Constants.DB_MESSAGES_FIELD_RECEIVER_ID + " = ?)",new String[] {
                sender_id, user_id, user_id, sender_id });
        db.close();
    }

    private ContentValues convertMessageToContentValues(MessageResponse message){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.DB_MESSAGES_FIELD_SENDER_ID, message.getSender().getId());
        contentValues.put(Constants.DB_MESSAGES_FIELD_RECEIVER_ID, message.getReceiver().getId());
        contentValues.put(Constants.DB_MESSAGES_FIELD_SENDER_NAME, message.getSender().getName());
        contentValues.put(Constants.DB_MESSAGES_FIELD_RECEIVER_NAME, message.getReceiver().getName());
        contentValues.put(Constants.DB_MESSAGES_FIELD_TEXT, message.getPlainText());
        contentValues.put(Constants.DB_MESSAGES_FIELD_SENT_AT, message.getSentAt().getTime());
        return contentValues;
    }

    private List<MessageResponse> convertCursorToMessages(Cursor cursor){
        List<MessageResponse> messages = new ArrayList<>();
        while (cursor.moveToNext()) {
            String senderId = cursor.getString(0);
            String receiverId = cursor.getString(1);
            String senderName = cursor.getString(2);
            String receiverName = cursor.getString(3);
            String text = cursor.getString(4);
            Timestamp sentAt = new Timestamp(cursor.getLong(5));
            Contact sender = new Contact(senderId, senderName);
            Contact receiver = new Contact(receiverId, receiverName);
            messages.add(new MessageResponse(sender, receiver, text, sentAt));
        }
        return messages;
    }
}
