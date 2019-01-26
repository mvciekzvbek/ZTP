package com.studia.tosi.cryptooverview;

import com.studia.tosi.cryptooverview.asymmetric.Blowfish;
import com.studia.tosi.cryptooverview.asymmetric.RSACipher;
import com.studia.tosi.cryptooverview.asymmetric.SecurityBlowfish;
import com.studia.tosi.cryptooverview.asymmetric.SecurityRSACipher;
import com.studia.tosi.cryptooverview.digitalsignature.DigitalSignature;
import com.studia.tosi.cryptooverview.digitalsignature.SecurityDigitalSignature;
import com.studia.tosi.cryptooverview.hash.Hash;
import com.studia.tosi.cryptooverview.hash.SecurityHash;
import com.studia.tosi.cryptooverview.symmetric.AESCipher;
import com.studia.tosi.cryptooverview.symmetric.DESCipher;
import com.studia.tosi.cryptooverview.symmetric.SecurityAESCipher;
import com.studia.tosi.cryptooverview.symmetric.SecurityDESCipher;
import org.bouncycastle.util.encoders.Hex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Base64;

public class CryptoOverview {
    private static String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

    private static void compareSymmetric(String message) throws Exception {
        byte[] msgBytes = message.getBytes();

        DESCipher des = new DESCipher();
        byte[] encryptedDES = des.encrypt(msgBytes);
        System.out.println("encryptedDES:\t" + new String(Base64.getEncoder().encode(encryptedDES)));
        byte[] decryptedDES = des.decrypt(encryptedDES);
        System.out.println("DecryptedDES: " + new String(decryptedDES));

        SecurityDESCipher des2 = new SecurityDESCipher();
        byte[] encryptedDES2 = des2.encrypt(msgBytes);
        System.out.println("encryptedDES2: " + new String(Base64.getEncoder().encode(encryptedDES2)));
        byte[] decryptedDES2 = des2.decrypt(encryptedDES2);
        System.out.println("DecryptedDES2: " + new String(decryptedDES2));

        AESCipher aes = new AESCipher();
        byte[] encryptedAES = aes.encrypt(msgBytes);
        System.out.println("encryptedAES: " + new String(Base64.getEncoder().encode(encryptedAES)));
        byte[] decryptedAES = aes.decrypt(encryptedAES);
        System.out.println("DecryptedAES: " + new String(decryptedAES));

        SecurityAESCipher aes2 = new SecurityAESCipher();
        byte[] encryptedAES2 = aes2.encrypt(msgBytes);
        System.out.println("encryptedAES2: " + new String(Base64.getEncoder().encode(encryptedAES2)));
        byte[] decryptedAES2 = aes2.decrypt(encryptedAES2);
        System.out.println("DecryptedAES2: " + new String(decryptedAES2));
    }

    private static void compareAsymmetric(String message) throws Exception {
        byte[] msgBytes = message.getBytes();

        SecurityRSACipher rsa = new SecurityRSACipher();
        byte[] encryptedRSA = rsa.encrypt(msgBytes);
        System.out.println("encryptedRSA: " + new String(Base64.getEncoder().encode(encryptedRSA)));
        byte[] decryptedRSA = rsa.decrypt(encryptedRSA);
        System.out.println("DecryptedRSA: " + new String(decryptedRSA));

        RSACipher rsa2 = new RSACipher();
        byte[] encryptedRSA2 = rsa2.encrypt(msgBytes);
        System.out.println("encryptedRSA2: " + new String(Base64.getEncoder().encode(encryptedRSA2)));
        byte[] decryptedRSA2 = rsa2.decrypt(encryptedRSA2);
        System.out.println("DecryptedRSA2: " + new String(decryptedRSA2));

        Blowfish blowfish = new Blowfish();
        byte[] encryptedBlowfish = blowfish.encrypt(msgBytes);
        System.out.println("encryptedBlowfish: " + new String(Base64.getEncoder().encode(encryptedBlowfish)));
        byte[] decryptedBlowfish = blowfish.decrypt(encryptedBlowfish);
        System.out.println("DecryptedBlowfish: " + new String(decryptedBlowfish));

        SecurityBlowfish blowfish2 = new SecurityBlowfish();
        byte[] encryptedBlowfish2 = blowfish2.encrypt(msgBytes);
        System.out.println("encryptedBlowfish2: " + new String(Base64.getEncoder().encode(encryptedBlowfish2)));
        byte[] decryptedBlowfish2 = blowfish2.decrypt(encryptedBlowfish2);
        System.out.println("DecryptedBlowfish2: " + new String(decryptedBlowfish2));
    }

    private static void compareHash(String message) throws NoSuchAlgorithmException {
        String sha256Hash = Hash.createSha256(message);
        System.out.println("SHA-256: " + sha256Hash);

        SecurityHash sh = new SecurityHash();
        String sha256Hash2 = sh.createSha256(message);
        System.out.println("SHA-256-2: " + sha256Hash2);


        String sha3256Hash = Hash.createSha3256(message);
        System.out.println("SHA3-256:" + sha3256Hash);
    }
//
    private static void compareSignature(String message) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        DigitalSignature digitalSignature = new DigitalSignature();
        byte[] signature = digitalSignature.sign(message.getBytes());
        System.out.println("Signature: " + new String(Hex.encode(signature)));
        boolean verification = digitalSignature.verifySignature(message.getBytes(), signature);
        System.out.println("Verification: " + verification);


        SecurityDigitalSignature digitalSignature2 = new SecurityDigitalSignature();
        byte[] signature2 = digitalSignature2.sign(message.getBytes());
        System.out.println("Signature: " + new String(Hex.encode(signature2)));
        boolean verification2 = digitalSignature2.verifySignature(message.getBytes(), signature2);
        System.out.println("Verification: " + verification2);


    }

    public static void main(String[] args) throws Exception {
        String message = "";
        try {
            message = readFile("test.txt");
        } catch (Exception e) {
            System.out.println(e);
        }

        compareSymmetric(message);
        compareAsymmetric(message);
        compareHash(message);
        compareSignature(message);
    }
}
