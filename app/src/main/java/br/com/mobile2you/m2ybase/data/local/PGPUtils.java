package br.com.mobile2you.m2ybase.data.local;

/**
 * Created by mayerlevy on 9/17/17.
 */


/**
 * Created by mayerlevy on 9/17/17.
 */

import org.spongycastle.bcpg.HashAlgorithmTags;
import org.spongycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.spongycastle.bcpg.sig.Features;
import org.spongycastle.bcpg.sig.KeyFlags;
import org.spongycastle.crypto.generators.RSAKeyPairGenerator;
import org.spongycastle.crypto.params.RSAKeyGenerationParameters;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.openpgp.PGPCompressedData;
import org.spongycastle.openpgp.PGPCompressedDataGenerator;
import org.spongycastle.openpgp.PGPEncryptedData;
import org.spongycastle.openpgp.PGPEncryptedDataGenerator;
import org.spongycastle.openpgp.PGPEncryptedDataList;
import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPKeyPair;
import org.spongycastle.openpgp.PGPKeyRingGenerator;
import org.spongycastle.openpgp.PGPLiteralData;
import org.spongycastle.openpgp.PGPLiteralDataGenerator;
import org.spongycastle.openpgp.PGPObjectFactory;
import org.spongycastle.openpgp.PGPOnePassSignatureList;
import org.spongycastle.openpgp.PGPPrivateKey;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPPublicKeyRingCollection;
import org.spongycastle.openpgp.PGPSecretKey;
import org.spongycastle.openpgp.PGPSecretKeyRing;
import org.spongycastle.openpgp.PGPSecretKeyRingCollection;
import org.spongycastle.openpgp.PGPSignature;
import org.spongycastle.openpgp.PGPSignatureGenerator;
import org.spongycastle.openpgp.PGPSignatureList;
import org.spongycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.spongycastle.openpgp.PGPSignatureSubpacketVector;
import org.spongycastle.openpgp.PGPUtil;
import org.spongycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.spongycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.spongycastle.openpgp.operator.PBESecretKeyEncryptor;
import org.spongycastle.openpgp.operator.PGPDigestCalculator;
import org.spongycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.spongycastle.openpgp.operator.bc.BcPBESecretKeyEncryptorBuilder;
import org.spongycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.spongycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.spongycastle.openpgp.operator.bc.BcPGPKeyPair;
import org.spongycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;
import org.spongycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.spongycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
import org.spongycastle.util.encoders.Base64;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyEncryptedData;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import static br.com.mobile2you.m2ybase.data.local.PGPManager.DEFAULT_KEYSTORE_FILE;

public class PGPUtils {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public final static PGPKeyRingGenerator generateKeyRingGenerator
            (String id, char[] pass)
            throws Exception {
        return generateKeyRingGenerator(id, pass, 0x60);
    }

    // Note: s2kcount is a number between 0 and 0xff that controls the
    // number of times to iterate the password hash before use. More
    // iterations are useful against offline attacks, as it takes more
    // time to check each password. The actual number of iterations is
    // rather complex, and also depends on the hash function in use.
    // Refer to Section 3.7.1.3 in rfc4880.txt. Bigger numbers give
    // you more iterations.  As a rough rule of thumb, when using
    // SHA256 as the hashing function, 0x10 gives you about 64
    // iterations, 0x20 about 128, 0x30 about 256 and so on till 0xf0,
    // or about 1 million iterations. The maximum you can go to is
    // 0xff, or about 2 million iterations.  I'll use 0xc0 as a
    // default -- about 130,000 iterations.

    public final static PGPKeyRingGenerator generateKeyRingGenerator
            (String id, char[] pass, int s2kcount)
            throws Exception {
        // This object generates individual key-pairs.
        RSAKeyPairGenerator kpg = new RSAKeyPairGenerator();

        // Boilerplate RSA parameters, no need to change anything
        // except for the RSA key-size (2048). You can use whatever
        // key-size makes sense for you -- 4096, etc.
        kpg.init
                (new RSAKeyGenerationParameters
                        (BigInteger.valueOf(0x10001),
                                new SecureRandom(), 2048, 12));

        // First create the master (signing) key with the generator.
        PGPKeyPair rsakp_sign =
                new BcPGPKeyPair
                        (PGPPublicKey.RSA_SIGN, kpg.generateKeyPair(), new Date());
        // Then an encryption subkey.
        PGPKeyPair rsakp_enc =
                new BcPGPKeyPair
                        (PGPPublicKey.RSA_ENCRYPT, kpg.generateKeyPair(), new Date());

        // Add a self-signature on the id
        PGPSignatureSubpacketGenerator signhashgen =
                new PGPSignatureSubpacketGenerator();

        signhashgen.setSignerUserID(false, id);

        // Add signed metadata on the signature.
        // 1) Declare its purpose
        signhashgen.setKeyFlags
                (false, KeyFlags.SIGN_DATA | KeyFlags.CERTIFY_OTHER);
        // 2) Set preferences for secondary crypto algorithms to use
        //    when sending messages to this key.
        signhashgen.setPreferredSymmetricAlgorithms
                (false, new int[]{
                        SymmetricKeyAlgorithmTags.AES_256,
                        SymmetricKeyAlgorithmTags.AES_192,
                        SymmetricKeyAlgorithmTags.AES_128
                });
        signhashgen.setPreferredHashAlgorithms
                (false, new int[]{
                        HashAlgorithmTags.SHA256,
                        HashAlgorithmTags.SHA1,
                        HashAlgorithmTags.SHA384,
                        HashAlgorithmTags.SHA512,
                        HashAlgorithmTags.SHA224,
                });
        // 3) Request senders add additional checksums to the
        //    message (useful when verifying unsigned messages.)
        signhashgen.setFeature
                (false, Features.FEATURE_MODIFICATION_DETECTION);

        // Create a signature on the encryption subkey.
        PGPSignatureSubpacketGenerator enchashgen =
                new PGPSignatureSubpacketGenerator();

        // Add metadata to declare its purpose
        enchashgen.setKeyFlags
                (false, KeyFlags.ENCRYPT_COMMS | KeyFlags.ENCRYPT_STORAGE);

        // Objects used to encrypt the secret key.
        PGPDigestCalculator sha1Calc =
                new BcPGPDigestCalculatorProvider()
                        .get(HashAlgorithmTags.SHA1);
        PGPDigestCalculator sha256Calc =
                new BcPGPDigestCalculatorProvider()
                        .get(HashAlgorithmTags.SHA256);

        // bcpg 1.48 exposes this API that includes s2kcount. Earlier
        // versions use a default of 0x60.
        PBESecretKeyEncryptor pske =
                (new BcPBESecretKeyEncryptorBuilder
                        (PGPEncryptedData.AES_256, sha256Calc, s2kcount))
                        .build(pass);

        // Finally, create the keyring itself. The constructor
        // takes parameters that allow it to generate the self
        // signature.
        PGPKeyRingGenerator keyRingGen =
                new PGPKeyRingGenerator
                        (PGPSignature.POSITIVE_CERTIFICATION, rsakp_sign,
                                id, sha1Calc, signhashgen.generate(), null,
                                new BcPGPContentSignerBuilder
                                        (rsakp_sign.getPublicKey().getAlgorithm(),
                                                HashAlgorithmTags.SHA1),
                                pske);

        // Add our encryption subkey, together with its signature.
        keyRingGen.addSubKey
                (rsakp_enc, enchashgen.generate(), null);
        return keyRingGen;
    }

    public static PGPSignature parseSignatureFromEncoded(InputStream in) {
        Object o;
        try {
            JcaPGPObjectFactory pgpFact = new JcaPGPObjectFactory(
                    PGPUtil.getDecoderStream(in));
            o = pgpFact.nextObject();
            if (o instanceof PGPCompressedData) {
                PGPCompressedData data = (PGPCompressedData) o;
                pgpFact = new JcaPGPObjectFactory(data.getDataStream());
                o = pgpFact.nextObject();
            }
        } catch (IOException | PGPException e) {
            e.printStackTrace();
            return null;
        }

        if (!(o instanceof PGPSignatureList)) {
            System.err.println("object not signature list");
            return null;
        }
        PGPSignatureList signList = (PGPSignatureList) o;
        if (signList.size() > 1) {
            System.err.println("more than one signature in signature list");
        }
        if (signList.isEmpty()) {
            System.err.println("signature list is empty");
            return null;
        }

        return signList.get(0);
    }

    public static PGPPublicKeyRing readPublicKeyRingFromStream(InputStream input) throws IOException, PGPException {
        PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(
                PGPUtil.getDecoderStream(input), new JcaKeyFingerprintCalculator());
        return pgpPub.getKeyRings().next();
    }

    public static PGPPublicKey getEncryptionKeyFromKeyRing(PGPPublicKeyRing keyRing) {
        Iterator<PGPPublicKey> keyIter = keyRing.getPublicKeys();
        while (keyIter.hasNext())
        {
            PGPPublicKey key = keyIter.next();
            if (key.isEncryptionKey()) {
                return key;
            }

        }
        return null;
    }

    static PGPSecretKey readSecretKey(String fileName) throws IOException, PGPException {
        InputStream keyIn = new BufferedInputStream(new FileInputStream(fileName));
        PGPSecretKey secKey = readSecretKey(keyIn);
        keyIn.close();
        return secKey;
    }

    /**
     * A simple routine that opens a key ring file and loads the first available key
     * suitable for signature generation.
     *
     * @param input stream to read the secret key ring collection from.
     * @return a secret key.
     * @throws IOException  on a problem with using the input stream.
     * @throws PGPException if there is an issue parsing the input stream.
     */
    static PGPSecretKey readSecretKey(InputStream input) throws IOException, PGPException {
        PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(
                PGPUtil.getDecoderStream(input), new JcaKeyFingerprintCalculator());

        //
        // we just loop through the collection till we find a key suitable for encryption, in the real
        // world you would probably want to be a bit smarter about this.
        //

        Iterator<PGPSecretKeyRing> keyRingIter = pgpSec.getKeyRings();
        while (keyRingIter.hasNext()) {
            PGPSecretKeyRing keyRing = keyRingIter.next();

            Iterator<PGPSecretKey> keyIter = keyRing.getSecretKeys();
            while (keyIter.hasNext()) {
                PGPSecretKey key = keyIter.next();

                if (key.isSigningKey()) {
                    return key;
                }
            }
        }

        throw new IllegalArgumentException("Can't find signing key in key ring.");
    }

    //
    // Private class method findSecretKey
    //
    public static PGPPrivateKey findSecretKey(InputStream keyIn, long keyID, char[] pass) throws IOException, PGPException, NoSuchProviderException {
        PGPSecretKeyRingCollection pgpSec = new PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(keyIn), new JcaKeyFingerprintCalculator());
        PGPSecretKey pgpSecKey = pgpSec.getSecretKey(keyID);
        if (pgpSecKey == null) {
            return null;
        }
        PBESecretKeyDecryptor decryptor = new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider()).build(pass);
        return pgpSecKey.extractPrivateKey(decryptor);
    }

    public static void printSignaturesFromKey(PGPPublicKey pubKey) throws PGPException, IOException {
        Iterator<PGPSignature> it = pubKey.getSignatures();
        System.out.println("The following users have signed this public key");
        while (it.hasNext()) {
            PGPSignature sig = it.next();
            PGPSignatureSubpacketVector subpacket = sig.getHashedSubPackets();
            System.out.println(subpacket.getSignerUserID());
        }
    }

    public static ArrayList<String> getSignaturesUserList(PGPPublicKey pubKey) {
        ArrayList<String> users = new ArrayList<>();
        Iterator<PGPSignature> it = pubKey.getSignatures();
        while(it.hasNext()) {
            PGPSignature sign = it.next();
            PGPSignatureSubpacketVector hashedPacket = sign.getHashedSubPackets();
            if (hashedPacket.getSignerUserID() != null) {
                users.add(hashedPacket.getSignerUserID());
            }
        }
        return users;
    }
}