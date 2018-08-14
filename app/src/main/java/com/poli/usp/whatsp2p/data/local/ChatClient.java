package com.poli.usp.whatsp2p.data.local;

/**
 * Created by mayerlevy on 9/30/17.
 */

import android.util.Log;

import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyRing;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.NoSuchProviderException;

import com.poli.usp.whatsp2p.data.remote.models.BaseResponse;
import com.poli.usp.whatsp2p.utils.exceptions.CouldNotEncryptException;

public class ChatClient {

    private String ip;
    private int port;
    private Socket client;

    public ChatClient() {
        this.client = new Socket();
    }

    public Boolean connect(String ip, int port) {
        try {
            Log.d("Chat", "Connecting to " + ip + ":" + port);
            client = new Socket();
            client.connect(new InetSocketAddress(ip, port), 2000);
            if (this.isConnected()) {
                this.ip = ip;
                this.port = port;
                Log.d("Chat", "Connected!");
                return true;
            } else {
                Log.d("Chat", "Couldn't connect to " + ip + ":" + port);
                return false;
            }
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Boolean sendMessage(BaseResponse message) {
        try {
            if (!this.isConnected()) {
                connect(this.ip, this.port);
            }
            if (this.isConnected()) {
                byte[] serializedMessage = Utils.serialize(message);
                PGPPublicKeyRing chatPublicKeyRing = PGPUtils.readPublicKeyRingFromStream(new ByteArrayInputStream(message.getReceiver().getChatPublicKeyRingEncoded()));
                PGPPublicKey chatPublicKey = PGPUtils.getEncryptionKeyFromKeyRing(chatPublicKeyRing);
                byte[] encryptedMessage = PGPManagerSingleton.getInstance().encrypt(serializedMessage, chatPublicKey);
                if (encryptedMessage == null) {
                    throw new CouldNotEncryptException();
                }
                DataOutputStream dOut = new DataOutputStream(this.client.getOutputStream());
                dOut.writeInt(encryptedMessage.length); // write length of the message
                dOut.write(encryptedMessage);            // write the message
                return true;
            }
        } catch (IOException | CouldNotEncryptException | NoSuchProviderException | PGPException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void shutdown() throws IOException {
        if (client != null) {
            client.close();
        }
    }

    public Boolean isConnected() {
        return client != null && client.isConnected();
    }
}