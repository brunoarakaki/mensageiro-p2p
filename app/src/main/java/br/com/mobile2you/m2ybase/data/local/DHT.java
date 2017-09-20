package br.com.mobile2you.m2ybase.data.local;

/**
 * Created by mayerlevy on 9/17/17.
 */

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import net.tomp2p.connection.Bindings;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.FutureRemove;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;

import br.com.mobile2you.m2ybase.data.remote.models.MessageResponse;

public class DHT {

    public Number160 serverId;
    public Number160 clientId;
    public PeerDHT serverPeer;
    public PeerDHT clientPeer;
    public Boolean started = false;
    public Boolean isConnected = false;
    public InetAddress connectedIP;
    public InetAddress ip;

    final int keyStore = 12345;
    final int serverPort = 4001;
    final int clientPort = 4002;

    public DHT() throws Exception {
        this((new Random()).nextInt());
    }

    public DHT(int id) throws Exception {

        this.serverId = new Number160(id);
        String serverIp = Utils.getIPAddress(true);
        try {
            this.ip = InetAddress.getByName(serverIp);
            Bindings b = new Bindings();
            b.addAddress(this.ip);
            this.serverPeer = new PeerBuilderDHT(new PeerBuilder(this.serverId).bindings(b).ports(serverPort).start()).start();
            this.started = true;
            Log.d("DHT", "Peer created. ID: " + this.serverId);
        } catch (Exception e) {
            Log.d("DHT", "Impossible to bind to this IP address");
        }
    }

    public Boolean connectTo(String address) throws IOException {
        if (!this.started) {
            Log.d("DHT", "DHT service not started!");
            return false;
        }
        this.clientId = new Number160((new Random()).nextInt());
        this.clientPeer = new PeerBuilderDHT(new PeerBuilder(this.clientId).ports(clientPort).behindFirewall().start()).start();
        PeerAddress bootStrapServer = new PeerAddress(Number160.ZERO, InetAddress.getByName(address), serverPort, serverPort, serverPort + 1);
        FutureDiscover fd = this.clientPeer.peer().discover().peerAddress(bootStrapServer).start();
        Log.d("DHT", "Trying to connect...");
        fd.awaitUninterruptibly();
        if (fd.isSuccess()) {
            Log.d("DHT", "Outside address: " + fd.peerAddress().inetAddress().getHostName());
        } else {
            Log.d("DHT", "Couldn't get outside address: " + fd.failedReason());
        }
        bootStrapServer = fd.reporter();
        FutureBootstrap bootstrap = this.clientPeer.peer().bootstrap().peerAddress(bootStrapServer).start();
        bootstrap.awaitUninterruptibly();
        if (bootstrap.isSuccess()) {
            this.isConnected = true;
            this.connectedIP = InetAddress.getByName(address);
            Log.d("DHT", "Connected!" + this.clientPeer.peerBean().peerMap().all());
            return true;
        } else {
            Log.d("DHT", "Could not connect!");
            return false;
        }
    }

    public MessageResponse get() throws ClassNotFoundException, IOException {
        if (!this.started) {
            Log.d("DHT", "DHT service not started!");
            return null;
        }
        try {
            FutureGet fget = serverPeer.get(new Number160(keyStore)).all().start();
            fget.awaitUninterruptibly();
            Iterator<Data> iterator = fget.dataMap().values().iterator();
            FutureGet fg;
            while (iterator.hasNext()) {
                Data d = iterator.next();
                int key = (Integer) d.object();
                fg = serverPeer.get(new Number160(key)).start();
                fg.awaitUninterruptibly();
                if (fg.data() != null) {
                    MessageResponse mes = (MessageResponse) fg.data().object();
                    Log.d("DHT", mes.getSender().getPeerId() + ">" + mes.getText());
                    serverPeer.remove(new Number160(key)).start();
                    return mes;
                }
            }
        } catch (IllegalArgumentException e) {
            Log.d("DHT", "DHT is offline");
        }
        return null;
    }

    public void send(MessageResponse mes) throws IOException {
        int r = new Random().nextInt();
        Log.d("DHT", "Message sent: " + mes.getText());
        clientPeer.add(new Number160(keyStore)).data(new Data(r)).start().awaitUninterruptibly();
        clientPeer.put(new Number160(r)).data(new Data(mes)).start().awaitUninterruptibly();
    }

    public void shutDown() {
        Log.d("DHT", "Shutting down");
        if (this.serverPeer != null) {
            this.serverPeer.shutdown();
        }
        if (this.clientPeer != null) {
            this.clientPeer.shutdown();
        }
    }
}