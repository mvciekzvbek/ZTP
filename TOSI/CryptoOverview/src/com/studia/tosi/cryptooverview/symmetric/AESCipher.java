package com.studia.tosi.cryptooverview.symmetric;


import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class AESCipher {

    private static final String AES_ALGORITHM = "AES";

    private AESEngine AESEngine;
    private CBCBlockCipher cbcBlockCipher;
    private PaddedBufferedBlockCipher cipher;
    private KeyGenerator keyGenerator;
    private KeyParameter keyParameter;

    public AESCipher() {
        AESEngine = new AESEngine();
        cbcBlockCipher = new CBCBlockCipher(AESEngine);
        cipher = new PaddedBufferedBlockCipher(cbcBlockCipher);

        try {
            keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Ciphering algorithm not found: " + e.getMessage());
        }

        createNewKey();
    }

    public void createNewKey() {
        SecretKey key = keyGenerator.generateKey();
        keyParameter = new KeyParameter(key.getEncoded());
    }

    public byte[] encrypt (byte[] message) {
        byte[] encryptedMessageBytes = new byte[0];

        try {
            cipher.init(true, keyParameter);
            encryptedMessageBytes = new byte[cipher.getOutputSize(message.length)];
            int length = cipher.processBytes(message, 0, message.length, encryptedMessageBytes, 0);
            cipher.doFinal(encryptedMessageBytes, length);
        } catch (InvalidCipherTextException e ) {
            // e.printStackTrace();
        }

        return encryptedMessageBytes;
    }

    public byte[] decrypt(byte[] message) {

        byte[] decryptedMessageBytes = new byte[0];

        try {
            cipher.init(false, keyParameter); //false for decryption
            decryptedMessageBytes = new byte[cipher.getOutputSize(message.length)];
            int length = cipher.processBytes(message, 0, message.length, decryptedMessageBytes, 0);
            cipher.doFinal(decryptedMessageBytes, length);
            return decryptedMessageBytes;
        } catch (InvalidCipherTextException e) {
            // e.printStackTrace();
        }

        return decryptedMessageBytes;
    }
}