package com.poli.usp.whatsp2p.data.local;

/**
 * Created by mayerlevy on 9/30/17.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.poli.usp.whatsp2p.Constants;
import com.poli.tcc.dht.DHT;

import net.tomp2p.dht.FuturePut;

import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPSignature;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.net.Socket;

import com.poli.usp.whatsp2p.data.remote.models.MessageResponse;
import com.poli.usp.whatsp2p.data.remote.models.SignatureResponse;

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
                    DataInputStream dIn = new DataInputStream(this.connection.getInputStream());

                    int length = dIn.readInt();                    // read length of incoming message
                    if(length > 0) {
                        byte[] message = new byte[length];
                        dIn.readFully(message, 0, message.length); // read the message
                        handleMessage(message);
                    }
                    Thread.sleep(100);
                } catch (EOFException e) {
                    // Nothing to read...
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    private void handleMessage(byte[] response) {
        try {
            Object decodedMessage = decodeMessage(response);
            if (decodedMessage instanceof MessageResponse) {
                MessageResponse message = (MessageResponse) decodedMessage;
                MessageDatabaseHelper dbHelper = new MessageDatabaseHelper(context);
                dbHelper.add(message);
                Intent intent = new Intent(Constants.FILTER_CHAT_RECEIVER);
                intent.putExtra("message", message);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            } else if (decodedMessage instanceof SignatureResponse) {
                SignatureResponse message = (SignatureResponse) decodedMessage;
                PGPSignature signature = PGPUtils.parseSignatureFromEncoded(new ByteArrayInputStream(message.getSignatureEncoded()));
                PGPPublicKeyRing pubKeyRing = PGPUtils.readPublicKeyRingFromStream(new ByteArrayInputStream(message.getPublicKeyRingEncoded()));
                PGPPublicKey signKey = PGPUtils.getSignKeyFromKeyRing(pubKeyRing);
                if (message.getTrust()) {
                    PGPPublicKey newKey = PGPManagerSingleton.getInstance().addSignatureToPublicKey(signature, signKey);
                    PGPUtils.printSignaturesFromKey(newKey);
                    PGPManagerSingleton.getInstance().updatePublicEncryptionKey(newKey);
                    FuturePut fput = DHT.putProtected("chatPublicKey", PGPManagerSingleton.getInstance().getPublicKeyRing().getEncoded());
                    if (fput.isSuccess()) {
                        Log.i("DHT", "[DHT] Chat public key updated");
                        Intent intent = new Intent(Constants.FILTER_SIGNATURE_UPDATE);
                        intent.putExtra("message", message);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    } else {
                        Log.i("DHT", "[DHT] Chat public key update failed: " + fput.failedReason());
                    }
                } else {
                    PGPPublicKey newKey = PGPManagerSingleton.getInstance().removePublicKeySignature(signKey);
                    PGPManagerSingleton.getInstance().updatePublicEncryptionKey(newKey);
                    FuturePut fput = DHT.putProtected("chatPublicKey", PGPManagerSingleton.getInstance().getPublicKeyRing().getEncoded());
                    if (fput.isSuccess()) {
                        Log.i("DHT", "[DHT] Chat public key updated");
                        Intent intent = new Intent(Constants.FILTER_SIGNATURE_UPDATE);
                        intent.putExtra("message", message);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    } else {
                        Log.i("DHT", "[DHT] Chat public key update failed: " + fput.failedReason());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorManager.handleError(e);
        }
    }

    private Object decodeMessage(byte[] message) throws Exception {
        String userPassword = PreferencesHelper.getInstance().getUserPassword();
        byte[] decryptedMessage = PGPManagerSingleton.getInstance().decrypt(message, userPassword.toCharArray());
        return Utils.deserialize(decryptedMessage);
    }
}