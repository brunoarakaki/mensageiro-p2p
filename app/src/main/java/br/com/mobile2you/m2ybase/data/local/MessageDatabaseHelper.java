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

    public List<MessageResponse> getMessagesFromContact(int user_id, int sender_id) {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        Cursor cursor =  db.rawQuery("select " + Constants.DB_MESSAGES_FIELD_SENDER_ID + ", " +
                Constants.DB_MESSAGES_FIELD_RECEIVER_ID + ", " +
                Constants.DB_MESSAGES_FIELD_TEXT + ", " +
                Constants.DB_MESSAGES_FIELD_SENT_AT + " from " +
                Constants.DB_MESSAGES_TABLE + " where (" +
                Constants.DB_MESSAGES_FIELD_SENDER_ID + " = ? AND " +
                Constants.DB_MESSAGES_FIELD_RECEIVER_ID + " = ?) OR (" +
                Constants.DB_MESSAGES_FIELD_SENDER_ID + " = ? AND " +
                Constants.DB_MESSAGES_FIELD_RECEIVER_ID + " = ?)",  new String[] {
                String.valueOf(sender_id), String.valueOf(user_id),
                String.valueOf(user_id), String.valueOf(sender_id) });
        return convertCursorToMessages(cursor);
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

    public void delete(long id) {
        SQLiteDatabase db = _openHelper.getWritableDatabase();
        if (db == null) {
            return;
        }
        db.delete(Constants.DB_MESSAGES_TABLE, "_id = ?", new String[] { String.valueOf(id) });
        db.close();
    }

    private ContentValues convertMessageToContentValues(MessageResponse message){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.DB_MESSAGES_FIELD_SENDER_ID, message.getSenderId());
        contentValues.put(Constants.DB_MESSAGES_FIELD_RECEIVER_ID, message.getReceiverId());
        contentValues.put(Constants.DB_MESSAGES_FIELD_TEXT, message.getText());
        contentValues.put(Constants.DB_MESSAGES_FIELD_SENT_AT, message.getSentAt().getTime());
        return contentValues;
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
