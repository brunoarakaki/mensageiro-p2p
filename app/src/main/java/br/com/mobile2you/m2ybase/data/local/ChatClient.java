package br.com.mobile2you.m2ybase.data.local;

/**
 * Created by mayerlevy on 9/30/17.
 */

import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import br.com.mobile2you.m2ybase.data.remote.models.MessageResponse;

public class ChatClient {

    private String ip;
    private int port;
    private Socket client;

    public ChatClient() {
        this.client = new Socket();
    }

    public Boolean connect(String ip, int port) {
        try {
            Log.d("Chat", "Connecting to " + ip + ":" + port);
            client = new Socket();
            client.connect(new InetSocketAddress(ip, port), 2000);
            if (this.isConnected()) {
                this.ip = ip;
                this.port = port;
                Log.d("Chat", "Connected!");
                return true;
            } else {
                Log.d("Chat", "Couldn't connect to " + ip + ":" + port);
                return false;
            }
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean sendMessage(MessageResponse message) {
        try {
            if (!this.isConnected()) {
                connect(this.ip, this.port);
            }
            if (this.isConnected()) {
                ObjectOutputStream oos = new ObjectOutputStream(this.client.getOutputStream());
                oos.writeObject(message);
                Log.d("Chat", "Message sent: " + message.getPlainText());
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void shutdown() throws IOException {
        if (client != null) {
            client.close();
        }
    }

    public Boolean isConnected() {
        return client != null && client.isConnected();
    }
}