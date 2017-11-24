package com.poli.tcc.dht;
/**
 * Created by mayerlevy on 9/17/17.
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Iterator;

import com.poli.tcc.dht.DHTException.UsernameAlreadyTakenException;

import net.tomp2p.connection.Bindings;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.FuturePut;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.dht.StorageLayer.ProtectionEnable;
import net.tomp2p.dht.StorageLayer.ProtectionMode;
import net.tomp2p.futures.BaseFutureListener;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.nat.FutureNAT;
import net.tomp2p.nat.PeerBuilderNAT;
import net.tomp2p.nat.PeerNAT;
import net.tomp2p.p2p.JobScheduler;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.p2p.RequestP2PConfiguration;
import net.tomp2p.p2p.builder.BootstrapBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.peers.PeerMapChangeListener;
import net.tomp2p.peers.PeerStatistic;
import net.tomp2p.relay.FutureRelay;
import net.tomp2p.replication.IndirectReplication;
import net.tomp2p.rpc.ObjectDataReply;
import net.tomp2p.storage.Data;
import net.tomp2p.tracker.TrackerBuilder;
import net.tomp2p.utils.Utils;

public class DHT {

    private static DHTNode me;
    public static PeerDHT serverPeer;
    public static PeerNAT serverPeerNAT;
    private static boolean started = false;
    private static boolean mVerified = false; 
    private static int MIN_CONNECTIONS = 3;
    private static int maxConnections() { return (int) Math.max(MIN_CONNECTIONS, Math.ceil(Math.log(serverPeer.peerBean().peerMap().size()))); }

    private static final int defaultPort = 4001;
    
    public static void start(DHTNode me, int port) throws DHTException.UsernameAlreadyTakenException, IOException, ClassNotFoundException {
    	
    	DHT.me = me;
    	final int serverPort = port != 0 ? port : defaultPort;
        me.setPort(serverPort);
        Bindings b = new Bindings();
        b.addAddress(Inet4Address.getByName(me.getIp()));
        serverPeer = new PeerBuilderDHT(new PeerBuilder(me.getPeerID()).keyPair(me.getSignKeyPair()).bindings(b).ports(serverPort).start()).start();
        System.out.println("Starting node...");
        System.out.println("[DHT] Peer created for address " + serverPeer.peerAddress() + ". Username: " + me.getUsername() +". Peer ID: " + me.getPeerID());
        started = true;
        serverPeer.peerBean().peerMap().addPeerMapChangeListener(new PeerMapChangeListener() {

			@Override
			public void peerInserted(final PeerAddress peer, boolean verified) {
				// TODO Auto-generated method stub
				if (verified) {
					System.out.println("[DHT] Peer found: " + peer);
					System.out.println("[DHT] Bootstrap bean: " + serverPeer.peer().distributedRouting().peerMap().all());
					System.out.println("[DHT] Peer bean: " + serverPeer.peerBean().peerMap().all());
				}
			}

			@Override
			public void peerRemoved(PeerAddress peer, PeerStatistic statistic) {
				// TODO Auto-generated method stub
				System.out.println("[DHT] Peer disconnected: " + peer);
				System.out.println("[DHT] Bootstrap bean: " + serverPeer.peer().distributedRouting().peerMap().all());
				System.out.println("[DHT] Peer bean: " + serverPeer.peerBean().peerMap().all());
			}

			@Override
			public void peerUpdated(PeerAddress arg0, PeerStatistic arg1) {
				// TODO Auto-generated method stub
				
			}
        	
        });
       
    }
    
    public static Boolean verify() throws UsernameAlreadyTakenException, IOException {
        // Add public key to the network
    	final KeyPair keyPair = serverPeer.peerBean().getKeyPair();
        Number160 locationKey = Number160.createHash(me.getUsername());
        Data publicKeyData = new Data(keyPair.getPublic());
        publicKeyData.protectEntry(keyPair);
        FuturePut keyPut = serverPeer.put(locationKey).keyPair(keyPair).data(publicKeyData).start().awaitUninterruptibly();
        if (keyPut.isFailed()) {
        	mVerified = false;
        	throw new DHTException.UsernameAlreadyTakenException();
        } else {
        	mVerified = true;
        	return true;
        }
    }

    public static Boolean connectTo(String ip, int port) throws IOException {
        if (!started) {
        	System.out.println("[DHT] DHT service not started!");
            return false;
        }
        int clientPort = port != 0 ? port : defaultPort;
        System.out.println("[DHT] Connecting to " + ip +":" + port + "...");
        
    	FutureDiscover fd = serverPeer.peer().discover().inetAddress(InetAddress.getByName(ip)).ports(clientPort).start();
        fd.awaitUninterruptibly();
        if (fd.isSuccess()) { 
        	System.out.println("[DHT] We are not behind a NAT and reachable to other peers. Outside address: " + fd.peerAddress());
        	System.out.println("[DHT] Bootstrap bean: " + serverPeer.peer().distributedRouting().peerMap().all());
        	System.out.println("[DHT] Peer bean: " + serverPeer.peerBean().peerMap().all());
        	return true;
        } else {
        	System.out.println("[DHT] Couldn't get outside address: " + fd.failedReason());
        }
        
        // Trying port forwarding...
        serverPeerNAT = new PeerBuilderNAT(serverPeer.peer()).start();
        
        FutureNAT fnat = serverPeerNAT.startSetupPortforwarding(fd);
        fnat.awaitUninterruptibly();
        if (fnat.isSuccess())
        {
            // Port forwarding has succeed
            System.out.println("[DHT] Port forwarding was successful. My address visible to the outside is " + fnat.peerAddress());
            System.out.println("[DHT] Bootstrap bean: " + serverPeer.peer().distributedRouting().peerMap().all());
            System.out.println("[DHT] Peer bean: " + serverPeer.peerBean().peerMap().all());
            return true;
        } else {
            System.out.println("[DHT] Port forwarding has failed. Reason: " + fnat.failedReason());
        }
        
        // Last resort: we try to use other peers as relays
        // The firewalled flags have to be set, so that other peers don’t add the unreachable peer to their peer maps.
        PeerAddress serverPeerAddress = serverPeer.peer().peerBean().serverPeerAddress();
        serverPeerAddress = serverPeerAddress.changeFirewalledTCP(true).changeFirewalledUDP(true);
        serverPeer.peer().peerBean().serverPeerAddress(serverPeerAddress);
        
        // Find neighbors
        FutureBootstrap futureBootstrap = serverPeer.peer().bootstrap().inetAddress(InetAddress.getByName(ip)).ports(clientPort).start();
        futureBootstrap.awaitUninterruptibly();
        if (futureBootstrap.isSuccess())
        {
            System.out.println("[DHT] Bootstrap was successful. bootstrapTo  = " + futureBootstrap.bootstrapTo());
            System.out.println("[DHT] Bootstrap bean: " + serverPeer.peer().distributedRouting().peerMap().all());
            System.out.println("[DHT] Peer bean: " + serverPeer.peerBean().peerMap().all());
            return true;
        } else {
        	System.out.println("[DHT] Bootstrap failed. Reason:" + futureBootstrap.failedReason());
        }
        return false;

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
        return new Number160(id.getBytes());
    }
    
    public static String getOriginalID(Number160 id) {
    	return new String(id.toByteArray()).trim();
    }
    
    public static PeerAddress lookupUser(String username) throws ClassNotFoundException, IOException {
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
    
    public static Object get(String location) throws ClassNotFoundException, IOException {
    	Number160 locationKey = Number160.createHash(location);
    	FutureGet fget = serverPeer.get(locationKey).start();
    	fget.awaitUninterruptibly();
    	if (fget.data() != null) {
    		return fget.data().object();
    	} else {
    		return null;
    	}
    }
    
    public static Object getProtected(String locationKey, PublicKey publicKey) throws ClassNotFoundException, IOException {
    	Number160 domainKey = Utils.makeSHAHash(publicKey.getEncoded());
    	Number160 location = Number160.createHash(locationKey);
    	FutureGet fget = serverPeer.get(location).domainKey(domainKey).start();
    	fget.awaitUninterruptibly();
    	if (fget.data() != null) {
    		return fget.data().object();
    	} else {
    		return null;
    	}
    }
    
    public static FuturePut put(String location, Object content) throws ClassNotFoundException, IOException {
    	Number160 locationKey = Number160.createHash(location);
    	FuturePut fput = serverPeer.put(locationKey).data(new Data(content)).start();
    	fput.awaitUninterruptibly();
    	return fput;
    }
    
    public static FuturePut putProtected(String location, Object content) throws ClassNotFoundException, IOException {
    	Number160 domainKey = Utils.makeSHAHash(me.getSignKeyPair().getPublic().getEncoded());
    	Number160 locationKey = Number160.createHash(location);
    	KeyPair keyPair = new KeyPair(me.getSignKeyPair().getPublic(), me.getSignKeyPair().getPrivate());
    	FuturePut fput = serverPeer.put(locationKey).keyPair(keyPair).data(new Data(content)).domainKey(domainKey).protectDomain().start();
    	fput.awaitUninterruptibly();
    	return fput;
    }

    public static DHTNode getMe() {
        return me;
    }
    
    public static byte[] encodePublicKey(PublicKey key) {       
        try {
        	ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream o =  new ObjectOutputStream(b);
			o.writeObject(key);
	        byte[] res = b.toByteArray();
	        o.close();
	        b.close();
	        return res;
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }
    
    public static PublicKey decodePublicKey(byte[] encoded) {
    	try {
	    	ByteArrayInputStream bi = new ByteArrayInputStream(encoded);
	        ObjectInputStream oi = new ObjectInputStream(bi);
	        Object obj = oi.readObject();
	        oi.close();
	        bi.close(); 
	        if (obj instanceof PublicKey) {
	        	return (PublicKey) obj;
	        }
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
    
}