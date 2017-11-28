package com.poli.usp.whatsp2p.data.local;

import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.poli.tcc.dht.DHT;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.poli.usp.whatsp2p.Constants;

/**
 * Created by mayerlevy on 10/1/17.
 */

public class NodeDiscovery {

    private NsdServiceInfo serviceInfo;
    private String mServiceName;
    private NsdManager mNsdManager;
    private Context mContext;
    private RegistrationListener mRegistrationListener;
    private DiscoveryListener mDiscoveryListener;

    public NodeDiscovery(Context context, int port) {
        mContext = context;
        serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(Constants.NSD_SERVICE_NAME);
        serviceInfo.setServiceType(Constants.NSD_SERVICE_TYPE);
        serviceInfo.setPort(port);
        mNsdManager = (NsdManager) mContext.getSystemService(Context.NSD_SERVICE);
        mRegistrationListener = new RegistrationListener();
        mDiscoveryListener = new DiscoveryListener();
        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }

    public void startLookup() {
        try {
            mNsdManager.discoverServices(Constants.NSD_SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d("[NSD]", "Network discovery off");
                    try {
                        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
                    } catch (IllegalArgumentException e) {
                        // Service already stopped;
                    }
                }
            }, 20000);
        } catch (IllegalArgumentException e) {
            // Service already in use;
        }
    }

    public void stopLookup() {
        try {
            mNsdManager.stopServiceDiscovery(mDiscoveryListener);
        } catch (IllegalArgumentException e) {
            // Service already stopped;
        }
    }

    private class RegistrationListener implements NsdManager.RegistrationListener {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                // Save the service name.  Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                mServiceName = NsdServiceInfo.getServiceName();
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed!  Put debugging code here to determine why.
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                // Service has been unregistered.  This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Unregistration failed.  Put debugging code here to determine why.
            }
        };

        // Instantiate a new DiscoveryListener
    private class DiscoveryListener implements NsdManager.DiscoveryListener {

        //  Called as soon as service discovery begins.
        @Override
        public void onDiscoveryStarted(String regType) {

        }

        @Override
        public void onServiceFound(NsdServiceInfo service) {
            // A service was found!  Do something with it.
             if (service.getServiceName().contains(Constants.NSD_SERVICE_NAME)){
                mNsdManager.resolveService(service, new ResolveListener());
            }
        }

        @Override
        public void onServiceLost(NsdServiceInfo service) {

        }

        @Override
        public void onDiscoveryStopped(String serviceType) {

        }

        @Override
        public void onStartDiscoveryFailed(String serviceType, int errorCode) {

        }

        @Override
        public void onStopDiscoveryFailed(String serviceType, int errorCode) {

        }
    };

    private class ResolveListener implements NsdManager.ResolveListener {

        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            // Called when the resolve fails.  Use the error code to debug.
            Log.e("[NSD]", "Resolve failed. Code " + errorCode);
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            if (serviceInfo.getServiceName().equals(mServiceName)) {
                return;
            }
            Log.d("[NSD]", "Found new peer on network: " + serviceInfo.getHost().getHostAddress());
            String ip = serviceInfo.getHost().getHostAddress();
            int port = serviceInfo.getPort();
            try {
                if (DHT.connectTo(ip, port)) {
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(Constants.FILTER_DHT_CONNECTION));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public void shutdown() {
        try {
            mNsdManager.unregisterService(mRegistrationListener);
        } catch (IllegalArgumentException e) {
            // Registration service already stopped
        }
        try {
            mNsdManager.stopServiceDiscovery(mDiscoveryListener);
        } catch (IllegalArgumentException e) {
            // Discovery service already stopped
        }
    }

}
