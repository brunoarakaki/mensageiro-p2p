package br.com.mobile2you.m2ybase.data.remote.models;

import org.spongycastle.openpgp.PGPSignature;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by mayerlevy on 10/30/17.
 */

public class SignatureResponse extends BaseResponse implements Serializable {

    private byte[] signatureEncoded;
    private String identifier;
    private boolean trust;

    public SignatureResponse(String identifier, PGPSignature signature, boolean trust) {
        this.identifier = identifier;
        this.setSignature(signature);
        this.trust = trust;
    }

    public byte[] getEncoded() {
        return signatureEncoded;
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

    public String getIdentifier() {
        return identifier;
    }

    public boolean getTrust() { return trust; }
}
