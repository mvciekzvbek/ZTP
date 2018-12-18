package com.studia.tosi.rsacipher;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bouncycastle.util.BigIntegers;

class RsaCipher {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private BigInteger one = new BigInteger("1");
    private BigInteger three = new BigInteger("3");
    private BigInteger four = new BigInteger("4");
    // n is 2048bit size which is 256bytes  => m < n => max size is 255 bytes
    private int size = 255;

    private BigInteger[] generatePrimes() {
        BigInteger[] primes = new BigInteger[2];
        BigInteger p, q;

        do {
            Random rnd = new Random();
            p = new BigInteger(1024, 3000, rnd);
            q = new BigInteger(1024, 3000, rnd);
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

        return (p.subtract(one)).multiply(q.subtract(one));
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
            euler = BigInteger.probablePrime(fi.bitLength(), rnd);
        } while (euler.compareTo(one) > 0 && euler.compareTo(fi.subtract(one)) < 0 && (euler.gcd(fi)).equals(one));

        return euler;
    }

    private BigInteger encryptPart(byte[] part, PublicKey publicKey) {
        BigInteger biPart = new BigInteger(1, part);
        return biPart.modPow(publicKey.n, publicKey.e);
    }

    private BigInteger decryptPart(BigInteger part) {
        return part.modPow(this.privateKey.d, this.privateKey.n);
    }

    private List<BigInteger> encryptImageBytes(byte[] imageBytes) {
        List<byte[]> devidedByteArray = divideByteArray(imageBytes, this.size);

        List<BigInteger> bigIntegers = new ArrayList<>();

        for (byte[] part : devidedByteArray) {
            bigIntegers.add(encryptPart(part, this.publicKey));
        }

        return bigIntegers;
    }

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

    public byte[] decrypt (List<BigInteger> encrypted) {

        List<BigInteger> decrypted = new ArrayList<>();

        for (BigInteger part : encrypted) {
            decrypted.add(decryptPart(part));
        }

        List <Byte> bytes = new ArrayList<Byte>();

        for (int i = 0, j = decrypted.size(); i < j; i++) {
            BigInteger part = decrypted.get(i);
            byte[] partAsBytes = BigIntegers.asUnsignedByteArray(part);

            for(byte b : partAsBytes) {
                bytes.add(b);
            }
        }

        return convertBytes(bytes);
    }

    private byte[] convertBytes(List<Byte> bytes) {
        byte[] ret = new byte[bytes.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = bytes.get(i);
        }
        return ret;
    }
}
