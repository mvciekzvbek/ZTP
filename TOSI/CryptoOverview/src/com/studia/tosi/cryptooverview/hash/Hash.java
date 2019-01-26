package com.studia.tosi.cryptooverview.hash;


import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;

public class Hash {
    private static SHA256.Digest sha256Digest = new SHA256.Digest();
    private static SHA3.Digest256 sha3256Digest = new SHA3.Digest256();;

    public static String createSha256(String message) {
        byte[] hashSHA256 = sha256Digest.digest(message.getBytes());
        return new String(Hex.encode(hashSHA256));
    }

    public static String createSha3256(String message) {
        byte[] hashSHA3256 = sha3256Digest.digest(message.getBytes());
        return new String(Hex.encode(hashSHA3256));
    }
}

