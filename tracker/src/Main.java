import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import com.poli.tcc.dht.DHT;
import com.poli.tcc.dht.DHTNode;
import com.poli.tcc.dht.Utils;

import net.tomp2p.peers.Number160;

public class Main {
	
	public static void main(String[] args) {
		String trackerName = "mainTracker";
		final Number160 peerId = DHT.createPeerID(trackerName);
		try {
			KeyPairGenerator gen = KeyPairGenerator.getInstance( "DSA" );
	        SecureRandom secRandom = SecureRandom.getInstance("SHA1PRNG");
	        gen.initialize(1024, secRandom);
	        KeyPair keyPair = gen.generateKeyPair();
			final DHTNode me = new DHTNode(peerId);
			me.setUsername(trackerName);
			me.setIp(Utils.getIPAddress(true));
			me.setSignKeyPair(keyPair);
			DHT.start(me, 0);
			System.out.println("[Tracker] Listening on " + me.getIp() + ":" + me.getPort());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

    public static PublicKey getKeyFromEncoded(byte[] encoded) {
        try {
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encoded);
            KeyFactory keyFactory = null;
            keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(pubKeySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }
	
}
