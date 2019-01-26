package com.studia.tosi.cryptooverview.digitalsignature;

import java.security.*;

public class SecurityDigitalSignature {
    private Signature signature;
    private SecureRandom secureRandom;
    private KeyPairGenerator keyPairGenerator;
    private KeyPair keyPair;

    public SecurityDigitalSignature() throws NoSuchAlgorithmException {
        signature = Signature.getInstance("SHA256withRSA");
        secureRandom = new SecureRandom();
        keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPair = keyPairGenerator.generateKeyPair();
    }

    public byte[] sign(byte[] message) throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
        signature.initSign(keyPair.getPrivate(), secureRandom);
        signature.update(message);
        return signature.sign();
    }


    public boolean verifySignature(byte[] message, byte[] digitalSignature) throws InvalidKeyException, SignatureException {
        signature.initVerify(keyPair.getPublic());
        signature.update(message);
        return signature.verify(digitalSignature);
    }
}
