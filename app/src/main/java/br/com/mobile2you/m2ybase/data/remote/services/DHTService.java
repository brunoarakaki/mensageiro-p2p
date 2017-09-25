package br.com.mobile2you.m2ybase.data.remote.services;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import net.tomp2p.peers.Number160;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import br.com.mobile2you.m2ybase.Constants;
import br.com.mobile2you.m2ybase.data.local.Contact;
import br.com.mobile2you.m2ybase.data.local.DHT;
import br.com.mobile2you.m2ybase.data.remote.models.MessageResponse;

/**
 * Created by mayerlevy on 9/19/17.
 */

public class DHTService extends IntentService {

    private Contact myself;
    private DHT dht;
    private Timer receiverTimer;

    public String[] trackersList = {"192.168.1.104"};

    public DHTService() {
        super("DHTService");
    }

    @Override
    public void onDestroy() {
        receiverTimer.cancel();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(dhtReceiver);
        this.dht.shutDown();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            dht = new DHT();
            dht.myself = (Contact) intent.getSerializableExtra("myself");
            for (String trackerAddress : trackersList) {
                Thread opThread = new Thread(new connectTo(trackerAddress));
                opThread.start();
            }
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
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    private BroadcastReceiver dhtReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int op = intent.getIntExtra("op", 0);
            Thread opThread;
            switch(op) {
                case Constants.DHT_OP_CONNECT_TO: {
                    final String ip = intent.getStringExtra("ip");
                    opThread = new Thread(new connectTo(ip));
                    opThread.start();
                    break;
                }

                case Constants.DHT_OP_SEND: {
                    final MessageResponse mes = (MessageResponse) intent.getSerializableExtra("message");
                    opThread = new Thread(new send(mes));
                    opThread.start();
                    break;
                }

                case Constants.DHT_OP_CLOSE_CONNECTION: {
                    final String ip = intent.getStringExtra("ip");
                    opThread = new Thread(new closeConnection(ip));
                    opThread.start();
                    break;
                }
            }
        }
    };

    private void checkMessages() {
        try {
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

    private class closeConnection implements Runnable {

        private String ip;
        public closeConnection(String ip) {
            this.ip = ip;
        }

        @Override
        public void run() {
            dht.closeConnection(ip);
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
