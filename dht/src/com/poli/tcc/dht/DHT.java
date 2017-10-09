package com.poli.tcc.dht;
/**
 * Created by mayerlevy on 9/17/17.
 */

import java.io.IOException;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Iterator;

import net.tomp2p.connection.Bindings;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.FuturePut;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.BaseFutureListener;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.peers.PeerMapChangeListener;
import net.tomp2p.peers.PeerStatistic;
import net.tomp2p.storage.Data;

public class DHT {

    private static DHTNode me;
    private static PeerDHT serverPeer;
    private static Boolean started;

    private static final int defaultPort = 4001;
    
    public static void start(DHTNode me, int port) throws Exception {

    	DHT.me = me;
    	final int serverPort = port != 0 ? port : defaultPort;
        me.setPort(serverPort);
        Bindings b = new Bindings();
        b.addAddress(Inet4Address.getByName(me.getIp()));
        serverPeer = new PeerBuilderDHT(new PeerBuilder(me.getPeerID()).bindings(b).ports(serverPort).start()).start();
        System.out.println("Starting node...");
        FutureDiscover fd = serverPeer.peer().discover().inetAddress(Inet4Address.getByName(me.getIp())).ports(serverPort).start();
        fd.awaitUninterruptibly();
        PeerAddress outsideAddress = fd.reporter();
        // Add basic user information to the network
        serverPeer.put(me.getPeerID().xor(Number160.createHash("username"))).data(new Data(me.getUsername())).start().awaitUninterruptibly();
        serverPeer.put(me.getPeerID().xor(Number160.createHash("address"))).data(new Data(outsideAddress)).start().awaitUninterruptibly();
        System.out.println("[DHT] " + outsideAddress);
        System.out.println("[DHT] Peer created for address " + outsideAddress.inetAddress().getHostAddress()+":"+outsideAddress.tcpPort() + ". Username: " + me.getUsername() +". Peer ID: " + me.getPeerID());
        started = true;
        serverPeer.peerBean().peerMap().addPeerMapChangeListener(new PeerMapChangeListener() {

			@Override
			public void peerInserted(final PeerAddress peer, boolean verified) {
				// TODO Auto-generated method stub
				if (verified) {
					System.out.println("[DHT] Peer " + peer.peerId() + " connected");
//					serverPeer.peer().bootstrap().inetAddress(peer.inetAddress()).ports(peer.tcpPort()).start().addListener(new BaseFutureListener<FutureBootstrap>() {
//
//						@Override
//						public void exceptionCaught(Throwable e) throws Exception {
//							e.printStackTrace();
//						}
//
//						@Override
//						public void operationComplete(FutureBootstrap bootstrap) throws Exception {
//							if (bootstrap.isSuccess()) {
//					            System.out.println("[DHT] Bootstraped to " + peer.peerId());
//					            System.out.println("[DHT] Visible peers: " + serverPeer.peerBean().peerMap().all());
//					        } else {
//					        	System.out.println("[DHT] Could not bootstrap! " + bootstrap.failedReason());
//					        }
//						}
//						
//					});
				}
			}

			@Override
			public void peerRemoved(PeerAddress peer, PeerStatistic statistic) {
				// TODO Auto-generated method stub
				System.out.println("[DHT] Peer " + peer.peerId() + " disconnected");
			}

			@Override
			public void peerUpdated(PeerAddress arg0, PeerStatistic arg1) {
				// TODO Auto-generated method stub
				
			}
        	
        });
    }
    
    public static void start(DHTNode me) throws Exception {
    	start(me, 0);
    }

    public static Boolean connectTo(String ip, int port) throws IOException {
        if (!started) {
        	System.out.println("[DHT] DHT service not started!");
            return false;
        }
        int clientPort = port != 0 ? port : defaultPort;
        System.out.println("[DHT] Connecting to " + ip +":" + port + "...");
        PeerAddress bootStrapServer;
        FutureDiscover fd = serverPeer.peer().discover().inetAddress(InetAddress.getByName(ip)).ports(clientPort).start();
        fd.awaitUninterruptibly();
        if (fd.isSuccess()) { 
        	System.out.println("[DHT] Outside address: " + fd.peerAddress().inetAddress().getHostName());	
        } else {
        	System.out.println("[DHT] Couldn't get outside address: " + fd.failedReason());
        }
        bootStrapServer = fd.reporter();
        FutureBootstrap bootstrap = serverPeer.peer().bootstrap().peerAddress(bootStrapServer).start();
        System.out.println("[DHT] Bootstrap: " + bootStrapServer);
        bootstrap.awaitUninterruptibly();
        if (bootstrap.isSuccess()) {
            System.out.println("[DHT] Connected!");
            System.out.println("[DHT] All server peers: " + serverPeer.peerBean().peerMap().all());
            return true;
        } else {
        	System.out.println("[DHT] Could not connect! " + bootstrap.failedReason());
            return false;
        }

    }

    public static void closeConnection(DHTNode client) {
        Iterator<PeerAddress> it = serverPeer.peerBean().peerMap().all().iterator();
        while (it.hasNext()) {
        	PeerAddress peerAddress = it.next();
        	if (peerAddress.peerId().equals(client.getPeerID())) {
        		serverPeer.peer().peerBean().peerConnection(client.getPeerID()).close().awaitUninterruptibly();
        	}
        }
    }

    public static void shutDown() {
    	System.out.println("[DHT] Shutting down");
    	try {
	        if (serverPeer != null) {
	            serverPeer.peer().announceShutdown().start().awaitUninterruptibly();
	            serverPeer.shutdown();
	            started = false;
	        }
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

    public static Number160 createPeerID(String id) {
        return new Number160(String.format("0x%02x", new BigInteger(1, id.getBytes())));
    }
    
    public static PeerAddress lookupUser(String username) throws ClassNotFoundException, IOException {
//    	FutureGet fget = serverPeer.get(createPeerID(username).xor(Number160.createHash("address"))).start();
//    	fget.awaitUninterruptibly();
//    	if (fget.data() != null) {
//    		return (PeerAddress) fget.data().object();
//    	} else {
//    		return null;
//    	}
    	Number160 peerId = DHT.createPeerID(username);
    	Iterator<PeerAddress> it = serverPeer.peerBean().peerMap().all().iterator();
    	while (it.hasNext()) {
    		PeerAddress address = it.next();
    		if (address.peerId().equals(peerId)) {
    			return address;
    		}
    	}
    	return null;
    }
    
    public static Object get(String username, String identifier) throws ClassNotFoundException, IOException {
    	FutureGet fget = serverPeer.get(createPeerID(username).xor(Number160.createHash(identifier))).start();
    	fget.awaitUninterruptibly();
    	if (fget.data() != null) {
    		return fget.data().object();
    	} else {
    		return null;
    	}
    }
    
    public static void put(String username, String identifier, Object content) throws ClassNotFoundException, IOException {
    	FuturePut fput = serverPeer.put(createPeerID(username).xor(Number160.createHash(identifier))).data(new Data(content)).start();
    	fput.awaitUninterruptibly();
    }

    public static DHTNode getMe() {
        return me;
    }
}