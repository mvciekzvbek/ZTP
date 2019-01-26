package com.studia.tosi.cryptooverview.digitalsignature;

import com.studia.tosi.cryptooverview.asymmetric.RSACipher;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.signers.RSADigestSigner;

public class DigitalSignature {
    private RSACipher rsaCipher;
    private RSADigestSigner signer;

    public DigitalSignature() {
        rsaCipher = new RSACipher();
        signer = new RSADigestSigner(new SHA256Digest());
    }

    public byte[] sign(byte[] message) {
        AsymmetricKeyParameter privateKey = rsaCipher.getPrivateKey();
        signer.init(true, privateKey);
        signer.update(message, 0, message.length);

        byte[] signature;

        try {
            signature = signer.generateSignature();
        } catch (Exception ex) {
            throw new RuntimeException("Cannot generate RSA signature: " + ex.getMessage(), ex);
        }

        return signature;
    }

    public boolean verifySignature(byte[] message, byte[] signature) {
        AsymmetricKeyParameter publicKey = rsaCipher.getPublicKey();
        signer.init(false, publicKey);
        signer.update(message, 0, message.length);
        return signer.verifySignature(signature);
    }
}
