package com.poli.usp.whatsp2p.data.local;

/**
 * Created by mayerlevy on 9/30/17.
 */

import android.content.Context;
import android.util.Log;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer implements Runnable {

    private int port;
    private List<Socket> clients;
    private Context context;

    public ChatServer(int port, Context context) {
        this.port = port;
        this.context = context;
        this.clients = new ArrayList<>();
    }

    @Override
    public void run()  {
        try{
            ServerSocket server = new ServerSocket(this.port);
            Log.d("Chat", "Listening on port " + port);

            while (true) {
                Socket client = server.accept();
                Thread receiver = new Thread(new ChatReceiver(client, context));
                receiver.start();
                Log.d("Chat", "New connection with client " +
                        client.getInetAddress().getHostAddress());

                this.clients.add(client);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
