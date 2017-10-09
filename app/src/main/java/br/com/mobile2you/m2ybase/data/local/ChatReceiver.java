package br.com.mobile2you.m2ybase.data.local;

/**
 * Created by mayerlevy on 9/30/17.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.Socket;

import br.com.mobile2you.m2ybase.Constants;
import br.com.mobile2you.m2ybase.data.remote.models.MessageResponse;

public class ChatReceiver implements Runnable {

    private Socket connection;
    private Context context;

    public ChatReceiver(Socket connection, Context context) {
        this.connection = connection;
        this.context = context;
    }

    public void run() {

            while(true) {
                try {
                    if (connection.isClosed()) {
                        break;
                    }
                    ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());
                    MessageResponse mes = (MessageResponse) ois.readObject();
                    MessageDatabaseHelper dbHelper = new MessageDatabaseHelper(context);
                    dbHelper.add(mes);
                    Intent intent = new Intent(Constants.FILTER_CHAT_RECEIVER);
                    intent.putExtra("message", mes);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    Thread.sleep(100);
                } catch (EOFException e) {
                    // Nothing to read...
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }
}