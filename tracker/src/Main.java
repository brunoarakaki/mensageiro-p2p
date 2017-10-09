import com.poli.tcc.dht.DHT;
import com.poli.tcc.dht.DHTNode;
import com.poli.tcc.dht.Utils;

import net.tomp2p.peers.Number160;

public class Main {
	
	public static void main(String[] args) {
		String trackerName = "mainTracker";
		Number160 peerId = DHT.createPeerID(trackerName);
		try {
			DHTNode me = new DHTNode(peerId);
			me.setUsername(trackerName);
			me.setIp(Utils.getIPAddress(true));
			DHT.start(me);
			System.out.println("[Tracker] Listening on " + me.getIp() + ":" + me.getPort());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
