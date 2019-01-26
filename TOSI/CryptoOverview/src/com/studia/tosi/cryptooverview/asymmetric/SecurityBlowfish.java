package com.studia.tosi.cryptooverview.asymmetric;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class SecurityBlowfish {
    private KeyGenerator keyGenerator;
    private Key secretKey;
    private SecretKeySpec KS;
    private Cipher cipher;

    public SecurityBlowfish() throws NoSuchAlgorithmException, NoSuchPaddingException {
        keyGenerator = KeyGenerator.getInstance("Blowfish");

        keyGenerator.init(128);
        secretKey = keyGenerator.generateKey();

        cipher = Cipher.getInstance("Blowfish");
    }

    public byte[] encrypt(byte[] message) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return cipher.doFinal(message);
    }

    public byte[] decrypt(byte[] message) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return cipher.doFinal(message);
    }
}
