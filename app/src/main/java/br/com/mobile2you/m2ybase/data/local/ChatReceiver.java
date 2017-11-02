package br.com.mobile2you.m2ybase.data.local;

/**
 * Created by mayerlevy on 9/30/17.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.poli.tcc.dht.DHT;

import net.tomp2p.dht.FuturePut;

import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPSignature;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.Socket;

import br.com.mobile2you.m2ybase.Constants;
import br.com.mobile2you.m2ybase.data.remote.models.BaseResponse;
import br.com.mobile2you.m2ybase.data.remote.models.MessageResponse;
import br.com.mobile2you.m2ybase.data.remote.models.SignatureResponse;

public class ChatReceiver implements Runnable {

    private Socket connection;
    private Context context;

    public ChatReceiver(Socket connection, Context context) {
        this.connection = connection;
        this.context = context;
    }

    public void run() {

            while(true) {
                try {
                    if (connection.isClosed() || !connection.isConnected()) {
                        break;
                    }
                    ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());
                    BaseResponse response = (BaseResponse) ois.readObject();
                    handleMessage(response);
                    Thread.sleep(100);
                } catch (EOFException e) {
                    // Nothing to read...
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    private void handleMessage(BaseResponse response) {
        try {
            if (response instanceof MessageResponse) {
                MessageResponse message = (MessageResponse) response;
                byte[] decryptedText = PGPManagerSingleton.getInstance().decrypt(message.getEncodedText(), "12345".toCharArray());
                message.setPlainText(new String(decryptedText));
                MessageDatabaseHelper dbHelper = new MessageDatabaseHelper(context);
                dbHelper.add(message);
                Intent intent = new Intent(Constants.FILTER_CHAT_RECEIVER);
                intent.putExtra("message", message);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            } else if (response instanceof SignatureResponse) {
                SignatureResponse message = (SignatureResponse) response;
                PGPSignature signature = PGPUtils.parseSignatureFromEncoded(new ByteArrayInputStream(message.getEncoded()));
                String identifier = message.getIdentifier();
                if (message.getTrust()) {
                    PGPPublicKey newKey = PGPManagerSingleton.getInstance().signPublicKey(identifier, signature);
                    PGPUtils.printSignaturesFromKey(newKey);
                    PGPManagerSingleton.getInstance().updatePublicKeyRing(newKey);
                    FuturePut fput = DHT.putProtected("chatPublicKey", PGPManagerSingleton.getInstance().getPublicKeyRing().getEncoded());
                    if (fput.isSuccess()) {
                        Log.i("DHT", "[DHT] Chat public key updated");
                    } else {
                        Log.i("DHT", "[DHT] Chat public key update failed: " + fput.failedReason());
                    }
                } else {
                    PGPPublicKey newKey = PGPManagerSingleton.getInstance().removePublicKeySignature(identifier, signature);
                    PGPManagerSingleton.getInstance().updatePublicKeyRing(newKey);
                    FuturePut fput = DHT.putProtected("chatPublicKey", PGPManagerSingleton.getInstance().getPublicKeyRing().getEncoded());
                    if (fput.isFailed()) {
                        Log.i("DHT", "[DHT] Chat public key update failed: " + fput.failedReason());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}