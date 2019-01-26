package com.studia.tosi.cryptooverview.asymmetric;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class Blowfish {
    private static final String BLOWFISH_ALGORITHM = "Blowfish";

    private BlowfishEngine blowfishEngine;
    private PaddedBufferedBlockCipher cipher;
    private KeyGenerator keyGenerator;
    private KeyParameter keyParameter;

    public Blowfish () {
        blowfishEngine = new BlowfishEngine();
        cipher = new PaddedBufferedBlockCipher(blowfishEngine);

        try {
            keyGenerator = KeyGenerator.getInstance(BLOWFISH_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Ciphering algorithm not found: " + e.getMessage());
        }

        createNewKey();
    }

    public void createNewKey() {
        SecretKey key = keyGenerator.generateKey();
        keyParameter = new KeyParameter(key.getEncoded());
    }

    public byte[] encrypt(byte[] message) {
        byte[] encryptedMessageBytes = new byte[0];

        try {
            cipher.init(true, keyParameter);
            encryptedMessageBytes = new byte[cipher.getOutputSize(message.length)];
            int length = cipher.processBytes(message, 0, message.length, encryptedMessageBytes, 0);
            cipher.doFinal(encryptedMessageBytes, length);
        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
        }

        return encryptedMessageBytes;
    }

    public byte[] decrypt(byte[] message) {
        byte[] decryptedMessageBytes = new byte[0];

        try {
            cipher.init(false, keyParameter);
            decryptedMessageBytes = new byte[cipher.getOutputSize(message.length)];
            int length = cipher.processBytes(message, 0, message.length, decryptedMessageBytes, 0);
            cipher.doFinal(decryptedMessageBytes, length);
        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
        }

        return decryptedMessageBytes;
    }
}
