package com.studia.tosi.rsacipher;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class RsaCipher {
    private PublicKey publicKey;
    private PrivateKey privateKey;

    private BigInteger[] generatePrimes() {
        BigInteger[] primes = new BigInteger[2];
        BigInteger three = new BigInteger("3");
        BigInteger four = new BigInteger("4");
        BigInteger p, q;

        do {
            Random rnd = new Random();
            p = new BigInteger(1024, 1, rnd);
            q = new BigInteger(1024, 1, rnd);
        } while (p.mod(four).equals(three) && q.mod(four).equals(three));

        primes[0] = p;
        primes[1] = q;

        return primes;
    }

    private BigInteger calculateN (BigInteger[] primes) {
        BigInteger p = primes[0];
        BigInteger q = primes[1];

        return p.multiply(q);
    }

    private BigInteger calculateFi (BigInteger[] primes) {
        BigInteger p = primes[0];
        BigInteger q = primes[1];

        return (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
    }

    private PublicKey createPublicKey (BigInteger n, BigInteger e) {
        return new PublicKey(n, e);
    }

    private PrivateKey createPrivateKey (BigInteger euler, BigInteger fi, BigInteger n) {
        BigInteger d = euler.modInverse(fi);
        return new PrivateKey(n, d);
    }

    private BigInteger calculateEuler(BigInteger fi) {
        BigInteger euler;
        do {
            Random rnd = new Random();
            euler = new BigInteger(fi.bitLength(), rnd);
        } while (euler.compareTo(fi.subtract(BigInteger.ONE)) < 0 && euler.compareTo(BigInteger.ONE) > 0 && euler.gcd(fi).equals(BigInteger.ONE));

        return euler;
    }

    private BigInteger encryptPart(byte[] part, PublicKey publicKey) {
        BigInteger biPart = new BigInteger(part);
        return biPart.modPow(publicKey.n, publicKey.e);
    }

    private List<BigInteger> encryptImageBytes(byte[] imageBytes) {
        // n is 2048bit size which is 256bytes  => m < n => max size is 255 bytes
        int size = 255;

        List<byte[]> devidedByteArray = divideByteArray(imageBytes, size);

        List<BigInteger> bigIntegers = new ArrayList<>();

        for (byte[] part : devidedByteArray) {
            System.out.println("part");
            bigIntegers.add(encryptPart(part, this.publicKey));
        }

        return bigIntegers;
    }

//    private byte[] decryptImage(List<BigInteger> encrypted) {
//
//
//    }

    public static List<byte[]> divideByteArray(byte[] source, int chunksize) {
        List<byte[]> result = new ArrayList<>();
        int start = 0;

        while (start < source.length) {
            int end = Math.min(source.length, start + chunksize);
            result.add(Arrays.copyOfRange(source, start, end));
            start += chunksize;
        }

        return result;
    }

    public List<BigInteger> encrypt (byte[] bytes) {
        byte[] imageBytes = bytes;

        BigInteger[] primes = generatePrimes();
        BigInteger n = calculateN(primes);
        BigInteger fi = calculateFi(primes);
        BigInteger euler = calculateEuler(fi);

        this.publicKey = createPublicKey(n, euler);
        this.privateKey = createPrivateKey(euler, fi, n);

        return encryptImageBytes(imageBytes);
    }

//    public byte[] decrypt (List<BigInteger> encrypted) {
//        return decryptImage(encrypted);
//    }
}
