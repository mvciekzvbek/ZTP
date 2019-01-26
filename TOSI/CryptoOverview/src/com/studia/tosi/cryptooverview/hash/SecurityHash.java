package com.studia.tosi.cryptooverview.hash;

import org.bouncycastle.util.encoders.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityHash {
    MessageDigest digest256 = MessageDigest.getInstance("SHA-256");

    public SecurityHash() throws NoSuchAlgorithmException {
    }

    public String createSha256(String message) {
        byte[] hash = digest256.digest(message.getBytes());

        return new String(Hex.encode(hash));
    }
}

