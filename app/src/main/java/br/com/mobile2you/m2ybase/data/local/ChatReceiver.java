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
                    if (connection.isClosed() || !connection.isConnected()) {
                        break;
                    }
                    ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());
                    MessageResponse message = (MessageResponse) ois.readObject();
                    byte[] decryptedText = Utils.decrypt(Utils.getPrivateKeyFromKeyStore(context, "RSA"), message.getEncodedText());
                    message.setPlainText(new String(decryptedText));
                    MessageDatabaseHelper dbHelper = new MessageDatabaseHelper(context);
                    dbHelper.add(message);
                    Intent intent = new Intent(Constants.FILTER_CHAT_RECEIVER);
                    intent.putExtra("message", message);
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