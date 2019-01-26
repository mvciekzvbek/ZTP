package com.studia.tosi.cryptooverview.symmetric;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class SecurityAESCipher {
    private Cipher ecipher;
    private Cipher dcipher;
    private SecretKey key;

    public SecurityAESCipher() throws Exception {
        key = KeyGenerator.getInstance("AES").generateKey();
        ecipher = Cipher.getInstance("AES");
        dcipher = Cipher.getInstance("AES");
    }

    public byte[] encrypt(byte[] message) throws Exception {
        ecipher.init(Cipher.ENCRYPT_MODE, key);
        return ecipher.doFinal(message);
    }

    public byte[] decrypt(byte[] message) throws Exception {
        dcipher.init(Cipher.DECRYPT_MODE, key);
        return dcipher.doFinal(message);
    }
}
