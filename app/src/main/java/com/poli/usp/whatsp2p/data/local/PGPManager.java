package com.poli.usp.whatsp2p.data.local;

/**
 * Created by mayerlevy on 10/30/17.
 */

import android.content.Context;

import org.spongycastle.openpgp.PGPCompressedData;
import org.spongycastle.openpgp.PGPEncryptedData;
import org.spongycastle.openpgp.PGPEncryptedDataGenerator;
import org.spongycastle.openpgp.PGPEncryptedDataList;
import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPKeyRingGenerator;
import org.spongycastle.openpgp.PGPLiteralData;
import org.spongycastle.openpgp.PGPLiteralDataGenerator;
import org.spongycastle.openpgp.PGPObjectFactory;
import org.spongycastle.openpgp.PGPOnePassSignatureList;
import org.spongycastle.openpgp.PGPPrivateKey;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyEncryptedData;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPSecretKey;
import org.spongycastle.openpgp.PGPSecretKeyRing;
import org.spongycastle.openpgp.PGPSignature;
import org.spongycastle.openpgp.PGPSignatureGenerator;
import org.spongycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.spongycastle.openpgp.PGPUtil;
import org.spongycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.spongycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.spongycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.spongycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.spongycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.spongycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;
import org.spongycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.spongycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import org.spongycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Iterator;

public class PGPManager {

    private Context mContext;
    private String myId;
    private PGPPublicKeyRing pubKeyRing;
    private PGPSecretKeyRing secKeyRing;

//    public static String DEFAULT_KEYSTORE_FILE = "keystore";

    public PGPManager(Context context, String identifier, char[] passwd) throws Exception {
        this.mContext = context;
        this.myId = identifier;

        try {
            FileInputStream pubFile = context.openFileInput(myId + ".pkr");
            FileInputStream secFile = context.openFileInput(myId + ".skr");
            this.pubKeyRing = new PGPPublicKeyRing(pubFile, new JcaKeyFingerprintCalculator());
            this.secKeyRing = new PGPSecretKeyRing(secFile, new JcaKeyFingerprintCalculator());
        } catch (FileNotFoundException e) {
            PGPKeyRingGenerator krgen = PGPUtils.generateKeyRingGenerator(identifier, passwd);
            this.pubKeyRing = krgen.generatePublicKeyRing();
            this.secKeyRing = krgen.generateSecretKeyRing();
            this.save();
        }
    }

    public void save() throws IOException {

        BufferedOutputStream pubout = new BufferedOutputStream
                (mContext.openFileOutput(myId + ".pkr", Context.MODE_PRIVATE));
        this.pubKeyRing.encode(pubout);
        pubout.close();

        BufferedOutputStream secout = new BufferedOutputStream
                (mContext.openFileOutput(myId + ".skr", Context.MODE_PRIVATE));
        this.secKeyRing.encode(secout);
        secout.close();
    }

    public PGPPublicKeyRing getPublicKeyRing() { return pubKeyRing; }

    public PGPPublicKey getEncryptionKey() {
        Iterator <PGPPublicKey> it = pubKeyRing.getPublicKeys();
        while (it.hasNext()) {
            PGPPublicKey key = it.next();
            if (key.isEncryptionKey()) {
                return key;
            }
        }
        return null;
    }

    public PGPPublicKey getSignKey() {
        Iterator <PGPPublicKey> it = pubKeyRing.getPublicKeys();
        while (it.hasNext()) {
            PGPPublicKey key = it.next();
            if (key.isMasterKey()) {
                return key;
            }
        }
        return null;
    }

    public PGPPrivateKey getPrivateKey(char[] pass) throws PGPException {
        PGPSecretKey pgpSecKey = secKeyRing.getSecretKey();
        if (pgpSecKey == null) {
            return null;
        }
        PBESecretKeyDecryptor decryptor = new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider()).build(pass);
        return pgpSecKey.extractPrivateKey(decryptor);
    }

    public String getUserId() {
        PGPPublicKey signKey = this.getSignKey();
        Iterator<String> it = signKey.getUserIDs();
        while (it.hasNext()) {
            return it.next();
        }
        return null;
    }

    public PGPSignature generateSignatureForPublicKey(PGPPublicKey pubKey, char[] pass) throws PGPException {
        PGPPublicKey thisKey = this.getSignKey();
        String userId = this.getUserId();
        PGPSignatureGenerator    sGen = new PGPSignatureGenerator(new BcPGPContentSignerBuilder(thisKey.getAlgorithm(), PGPUtil.SHA1));
        sGen.init(PGPSignature.SUBKEY_REVOCATION, this.getPrivateKey(pass));
        PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();
        spGen.setSignerUserID(false, userId);
        sGen.setHashedSubpackets(spGen.generate());
        PGPSignature certification = sGen.generateCertification(thisKey, pubKey);
        return certification;
    }

    public PGPPublicKey addSignatureToPublicKey(PGPSignature signature, PGPPublicKey signeerKey) throws PGPException, IOException {
        PGPPublicKey thisKey = this.getEncryptionKey();
        if (PGPUtils.publicKeyIsSignedBy(thisKey, signeerKey)) {
            return thisKey;
        }
        PGPPublicKey newKey = PGPPublicKey.addCertification(thisKey, signature);
        return newKey;
    }

    public PGPPublicKey removePublicKeySignature(PGPPublicKey pubKey) throws PGPException, IOException {
        PGPPublicKey thisKey = this.getEncryptionKey();
        Iterator<PGPSignature> it = thisKey.getSignatures();
        while (it.hasNext()) {
            PGPSignature sig = it.next();
            sig.init(new JcaPGPContentVerifierBuilderProvider(), pubKey);
            if (sig.getSignatureType() == PGPSignature.SUBKEY_REVOCATION && sig.verifyCertification(pubKey, thisKey)) {
                PGPPublicKey newKey = PGPPublicKey.removeCertification(thisKey, sig);
                return newKey;
            }
        }
        return thisKey;
    }

    public boolean hasSignedPublicKey(PGPPublicKey pubKey) throws PGPException {
        PGPPublicKey thisKey = this.getSignKey();
        if (PGPUtils.publicKeyIsSignedBy(pubKey, thisKey)) {
            return true;
        }
        return false;
    }

    public void updatePublicEncryptionKey(PGPPublicKey newKey) throws IOException {
        PGPPublicKey thisKey = this.getEncryptionKey();
        pubKeyRing = PGPPublicKeyRing.removePublicKey(pubKeyRing, thisKey);
        pubKeyRing = PGPPublicKeyRing.insertPublicKey(pubKeyRing, newKey);
        secKeyRing = PGPSecretKeyRing.replacePublicKeys(secKeyRing, pubKeyRing);
        this.save();
    }

    public byte[] encrypt(byte[] rawData, PGPPublicKey encKey) throws IOException, NoSuchProviderException, PGPException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        boolean withIntegrityCheck = true;
        System.out.println("using PGPEncryptedDataGenerator...");
        // object that encrypts the data
        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();
        byte[] buffer = new byte[1 << 16];
        OutputStream pOut = lData.open(bOut, PGPLiteralData.BINARY, PGPLiteralData.CONSOLE, PGPLiteralData.NOW, buffer);
        byte[] buf = new byte[buffer.length];
        int len;
        ByteArrayInputStream in = new ByteArrayInputStream(rawData);
        while ((len = in.read(buf)) > 0) {
            pOut.write(buf, 0, len);
        }
        lData.close();
        pOut.close();
        PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(new JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5).setWithIntegrityPacket(withIntegrityCheck).setSecureRandom(new SecureRandom()).setProvider("SC"));
        cPk.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(encKey).setProvider("SC"));
        System.out.println("used PGPEncryptedDataGenerator...");
        // take the outputstream of the original file and turn it into a byte
        // array
        System.out.println("wrote bOut to byte array...");
        // write the plain text bytes to the armored outputstream
        byte[] bytes = bOut.toByteArray();
        OutputStream cOut = cPk.open(out, bytes.length);
        cOut.write(bytes);
        cPk.close();
        out.close();
        return out.toByteArray();
    }

    public byte[] decrypt(byte[] encdata, char[] passwd) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(encdata);
        InputStream in = PGPUtil.getDecoderStream(bais);
        try {
            JcaPGPObjectFactory pgpF = new JcaPGPObjectFactory(in);
            PGPEncryptedDataList enc;
            Object o = pgpF.nextObject();
            //
            // the first object might be a PGP marker packet.
            //
            if (o instanceof PGPEncryptedDataList) {
                enc = (PGPEncryptedDataList) o;
            } else {
                enc = (PGPEncryptedDataList) pgpF.nextObject();
            }
            //
            // find the secret key
            //
            Iterator<PGPPublicKeyEncryptedData> it = enc.getEncryptedDataObjects();
            PGPPrivateKey sKey = null;
            PGPPublicKeyEncryptedData pbe = null;
            while (sKey == null && it.hasNext()) {
                pbe = it.next();
                System.out.println("pbe id=" + pbe.getKeyID());
                ByteArrayInputStream secKeyStream = new ByteArrayInputStream(this.secKeyRing.getEncoded());
                sKey = PGPUtils.findSecretKey(secKeyStream, pbe.getKeyID(), passwd);
            }
            if (sKey == null) {
                throw new IllegalArgumentException("secret key for message not found.");
            }

            InputStream clear = pbe.getDataStream(new BcPublicKeyDataDecryptorFactory(sKey));
            PGPObjectFactory plainFact = new JcaPGPObjectFactory(clear);
            Object message = plainFact.nextObject();
            if (message instanceof PGPCompressedData) {
                PGPCompressedData cData = (PGPCompressedData) message;
                PGPObjectFactory pgpFact = new JcaPGPObjectFactory(cData.getDataStream());
                message = pgpFact.nextObject();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (message instanceof PGPLiteralData) {
                PGPLiteralData ld = (PGPLiteralData) message;
                InputStream unc = ld.getInputStream();
                int ch;
                while ((ch = unc.read()) >= 0) {
                    baos.write(ch);
                }
            } else if (message instanceof PGPOnePassSignatureList) {
                throw new PGPException("encrypted message contains a signed message - not literal data.");
            } else {
                throw new PGPException("message is not a simple encrypted file - type unknown.");
            }
            if (pbe.isIntegrityProtected()) {
                if (!pbe.verify()) {
                    System.err.println("message failed integrity check");
                } else {
                    System.err.println("message integrity check passed");
                }
            } else {
                System.err.println("no message integrity check");
            }
            return baos.toByteArray();
        } catch (PGPException e) {
            System.err.println(e);
            if (e.getUnderlyingException() != null) {
                e.getUnderlyingException().printStackTrace();
            }
        }
        return null;
    }


}

