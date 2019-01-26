package com.studia.tosi.cryptooverview.asymmetric;

import javax.crypto.Cipher;
import java.security.*;

public class SecurityRSACipher {
    private KeyPair keyPair;
    private PublicKey pubKey;
    private PrivateKey privateKey;

    public SecurityRSACipher() throws NoSuchAlgorithmException {
        keyPair = buildKeyPair();
        pubKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    public static KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        final int keySize = 2048;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        return keyPairGenerator.genKeyPair();
    }

    public byte[] encrypt(byte[] message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        return cipher.doFinal(message);
    }

    public byte[] decrypt(byte [] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, pubKey);

        return cipher.doFinal(encrypted);
    }
}
