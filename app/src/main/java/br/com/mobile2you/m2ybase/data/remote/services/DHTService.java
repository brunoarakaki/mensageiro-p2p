package br.com.mobile2you.m2ybase.data.remote.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import br.com.mobile2you.m2ybase.data.local.DHT;

/**
 * Created by mayerlevy on 9/19/17.
 */

public class DHTService extends Service {

    private DHT dht;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            this.dht = new DHT();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
