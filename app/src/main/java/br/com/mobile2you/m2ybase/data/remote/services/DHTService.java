package br.com.mobile2you.m2ybase.data.remote.services;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import br.com.mobile2you.m2ybase.Constants;
import br.com.mobile2you.m2ybase.data.local.DHT;
import br.com.mobile2you.m2ybase.data.remote.models.MessageResponse;
import br.com.mobile2you.m2ybase.ui.chat.ChatActivity;

/**
 * Created by mayerlevy on 9/19/17.
 */

public class DHTService extends IntentService {

    private DHT dht;
    private Timer receiverTimer;

    public DHTService() {
        super("DHTService");
    }

    @Override
    public void onCreate() {
        try {
            dht = new DHT();
            IntentFilter filter = new IntentFilter(Constants.RECEIVER_DHT_FILTER);
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(dhtReceiver, filter);
            receiverTimer = new Timer();
            receiverTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    checkMessages();
                }
            }, 0, 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        receiverTimer.cancel();
        this.dht.shutDown();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    private Boolean connectTo(String ip) {
        try {
            return this.dht.connectTo(ip);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private BroadcastReceiver dhtReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int op = intent.getIntExtra("op", 0);
            Thread opThread;
            switch(op) {
                case Constants.DHT_OP_CONNECT_TO:
                    final String ip = intent.getStringExtra("ip");
                    opThread = new Thread(new connectTo(ip));
                    opThread.start();
                    break;

                case Constants.DHT_OP_SEND:
                    final MessageResponse mes = (MessageResponse)intent.getSerializableExtra("message");
                    opThread = new Thread(new send(mes));
                    opThread.start();
                    break;
            }
        }
    };

    private void checkMessages() {
        try {
            Log.d("DHT", "Checking messages...");
            final MessageResponse message = dht.get();
            if (message != null) {
                Log.d("DHT", message.getSender().getName() + ": " + message.getText());
                Intent in = new Intent(Constants.RECEIVER_CHAT_FILTER);
                in.putExtra("message", message);
                LocalBroadcastManager.getInstance(DHTService.this).sendBroadcast(in);
            }
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private class connectTo implements Runnable {

        private String ip;
        public connectTo(String ip) {
            this.ip = ip;
        }

        @Override
        public void run() {
            try {
                dht.connectTo(ip);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private class send implements Runnable {

        private MessageResponse mes;
        public send(MessageResponse mes) {
            this.mes = mes;
        }

        @Override
        public void run() {
            try {
                dht.send(mes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
