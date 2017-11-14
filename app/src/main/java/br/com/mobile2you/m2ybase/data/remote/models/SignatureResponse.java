package br.com.mobile2you.m2ybase.data.remote.models;

import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPSignature;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by mayerlevy on 10/30/17.
 */

public class SignatureResponse extends BaseResponse implements Serializable {

    private byte[] signatureEncoded;
    private byte[] publicKeyRingEncoded;
    private String identifier;
    private boolean trust;

    public SignatureResponse(String identifier, PGPSignature signature, PGPPublicKeyRing publicKeyRing, boolean trust) {
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
