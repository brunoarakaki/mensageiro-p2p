package com.poli.usp.whatsp2p.data.remote.models;

import com.poli.usp.whatsp2p.data.local.Contact;

import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPSignature;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by mayerlevy on 10/30/17.
 */

public class SignatureResponse extends BaseResponse implements Serializable {

    static final long serialVersionUID = 502;

    private byte[] signatureEncoded;
    private byte[] publicKeyRingEncoded;
    private String identifier;
    private boolean trust;

    public SignatureResponse(Contact sender, Contact receiver, String identifier, PGPSignature signature, PGPPublicKeyRing publicKeyRing, boolean trust) {
        super(sender, receiver);
        this.identifier = identifier;
        this.setSignature(signature);
        this.setPublicKeyRing(publicKeyRing);
        this.trust = trust;
    }

    public byte[] getSignatureEncoded() {
        return signatureEncoded;
    }

    public byte[] getPublicKeyRingEncoded() {
        return publicKeyRingEncoded;
    }

    public void setSignature(PGPSignature signature) {
        try {
            ByteArrayOutputStream outSig = new ByteArrayOutputStream();
            signature.encode(outSig);
            signatureEncoded = outSig.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPublicKeyRing(PGPPublicKeyRing publicKeyRing) {
        try {
            ByteArrayOutputStream outSig = new ByteArrayOutputStream();
            publicKeyRing.encode(outSig);
            publicKeyRingEncoded = outSig.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean getTrust() { return trust; }
}
