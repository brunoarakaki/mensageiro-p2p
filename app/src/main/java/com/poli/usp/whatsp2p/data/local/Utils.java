package com.poli.usp.whatsp2p.data.local;

/**
 * Created by mayerlevy on 9/17/17.
 */

import android.content.Context;

import com.poli.usp.whatsp2p.Constants;

import org.spongycastle.jce.X509Principal;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.x509.X509V3CertificateGenerator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.poli.usp.whatsp2p.utils.StringUtil;

public class Utils {

    static { Security.addProvider(new BouncyCastleProvider());  }

    static final String DEFAULT_ENTRY_PASSWORD = "password";

    /*
     * Get IP address from first non-localhost interface
     * @param ipv4  true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }

    public static int getAvailablePort() {
        try {
            ServerSocket socket = new ServerSocket(0);
            int port = socket.getLocalPort();
            socket.close();
            return port;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static KeyPair getKeyPairFromKeyStore(Context context, String type) {
        try {
            FileInputStream inputStream = null;
            try {
                inputStream = context.openFileInput("keystore.pem");
            } catch (FileNotFoundException e) {
                System.out.println("Keystore file not found. A new one will be created...");
            }
            KeyStore ks = KeyStore.getInstance("BKS", "SC");
            ks.load(inputStream, getUserPassword().toCharArray());
            KeyStore.ProtectionParameter protectionParameter = new KeyStore.PasswordProtection(getUserPassword().toCharArray());
            KeyStore.Entry entry = ks.getEntry(Constants.PGP_KEY_ALIAS + "_" + type, protectionParameter);
            if (inputStream != null) {
                inputStream.close();
            }
            if (entry == null) {
                return generateNewKeyPair(context, type);
            }
            PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
            PublicKey publicKey = ks.getCertificate(Constants.PGP_KEY_ALIAS + "_" + type).getPublicKey();
            return new KeyPair(publicKey, privateKey);
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | UnrecoverableEntryException | NoSuchProviderException | IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static KeyPair generateNewKeyPair(Context context, String type) {
        try {
            FileInputStream inputStream = null;
            String userPassword = PreferencesHelper.getInstance().getUserPassword();
            try {
                inputStream = context.openFileInput("keystore.pem");
            } catch (FileNotFoundException e) {
                System.out.println("Keystore file not found. A new one will be created...");
            }
            KeyStore ks = KeyStore.getInstance("BKS", "SC");
            ks.load(inputStream, getUserPassword().toCharArray());
            if (inputStream!= null) {
                inputStream.close();
            }

            KeyPairGenerator kpg = KeyPairGenerator.getInstance(type, "SC");
            SecureRandom secRandom = SecureRandom.getInstance("SHA1PRNG");
            kpg.initialize(1024, secRandom);

            KeyPair keyPair = kpg.generateKeyPair();
            X509Certificate cert = generateCertificate(type, keyPair);

            KeyStore.ProtectionParameter protectionParameter = new KeyStore.PasswordProtection(getUserPassword().toCharArray());
            KeyStore.PrivateKeyEntry entry = new KeyStore.PrivateKeyEntry(keyPair.getPrivate(), new X509Certificate[] {cert});
            ks.setEntry(Constants.PGP_KEY_ALIAS + "_" + type, entry, protectionParameter);
            FileOutputStream outputStream;
            outputStream = context.openFileOutput("keystore.pem", Context.MODE_PRIVATE);
            ks.store(outputStream, getUserPassword().toCharArray());
            outputStream.close();

            return keyPair;

        } catch (KeyStoreException | SignatureException | InvalidKeyException | NoSuchProviderException | CertificateException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;

    }

    private static X509Certificate generateCertificate(String type, KeyPair keyPair) throws SignatureException, InvalidKeyException {
        X509V3CertificateGenerator v3CertGen = new X509V3CertificateGenerator();
        v3CertGen.setSerialNumber(BigInteger.valueOf(Math.abs(new SecureRandom().nextInt())));
        v3CertGen.setIssuerDN(new X509Principal("CN=whatsp2p, OU=None, O=None L=None, C=None"));
        v3CertGen.setNotBefore(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30));
        v3CertGen.setNotAfter(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365*10)));
        v3CertGen.setSubjectDN(new X509Principal("CN=whatsp2p, OU=None, O=None L=None, C=None"));
        v3CertGen.setPublicKey(keyPair.getPublic());
        v3CertGen.setSignatureAlgorithm("SHA1with" + type);
        return v3CertGen.generateX509Certificate(keyPair.getPrivate());
    }

    public static PublicKey getPublicKeyFromEncoded(String type, byte[] encoded) {
        try {
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encoded);
            KeyFactory keyFactory = KeyFactory.getInstance(type, "SC");
            return keyFactory.generatePublic(pubKeySpec);
        } catch (NoSuchProviderException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getUserPassword() {
        String userPassword = PreferencesHelper.getInstance().getUserPassword();
        return StringUtil.isBlank(userPassword) ? DEFAULT_ENTRY_PASSWORD : userPassword;
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }
    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }

}