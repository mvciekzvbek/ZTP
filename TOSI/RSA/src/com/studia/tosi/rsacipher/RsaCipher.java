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

    private BigInteger encryptPart(byte[] part) {
        BigInteger biPart = new BigInteger(1, part);
        return biPart.modPow(this.publicKey.e, this.publicKey.n);
    }

    private BigInteger decryptPart(BigInteger part) {
        return part.modPow(this.privateKey.d, this.privateKey.n);
    }

    private List<BigInteger> encryptImageBytes(byte[] imageBytes) {
        List<byte[]> devidedByteArray = divideByteArray(imageBytes, this.size);

        List<BigInteger> bigIntegers = new ArrayList<>();

        for(int i = 0; i < devidedByteArray.size(); i++) {
            System.out.println("i = " + i + ", el size = " + devidedByteArray.get(i).length);
        }

        for (byte[] part : devidedByteArray) {
//            System.out.println(part.length);
            bigIntegers.add(encryptPart(part));
        }

//        System.out.println(bigIntegers.size());

        return bigIntegers;
    }

    private static List<byte[]> divideByteArray(byte[] source, int chunksize) {
        List<byte[]> result = new ArrayList<>();
        int i,j;

        for (i = 0, j = source.length; i <= j; i += chunksize) {
            result.add(Arrays.copyOfRange(source, i, i + chunksize));
        }

        return result;
    }


    private byte[] convertBytes(List<Byte> bytes) {
        byte[] ret = new byte[bytes.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = bytes.get(i);
        }
        return ret;
    }

    private boolean isLastPart(int index, List<BigInteger> decrypted) {
        return index == decrypted.size() - 1;
    }

    private byte[] removeAdditionalBytes(byte[] bytes) {
        System.out.println("Last part size: " + bytes.length);
        int additional = this.size - bytes.length;
        System.out.println("Additional: " + additional);
        byte[] ret = new byte[this.size];

        for(int i = 0; i < this.size; i++) {
            // System.out.println(i);
            ret[i] = bytes[i];
        }

        System.out.println("Ret array size: " + ret.length);

        return ret;
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

//        System.out.println(decrypted.size());


        List <Byte> bytes = new ArrayList<Byte>();

        for (int i = 0; i < decrypted.size(); i++) {
            BigInteger part = decrypted.get(i);
            byte[] partAsBytes = BigIntegers.asUnsignedByteArray(part);

//            System.out.println(partAsBytes.length);
//            System.out.println(this.size);

            if (partAsBytes.length < this.size && this.isLastPart(i, decrypted)) {
                System.out.println("Last, i = " + i + " , el size = " + partAsBytes.length);
                partAsBytes = removeAdditionalBytes(partAsBytes);
            } else {
                System.out.println("i = " + i + " , el size = " + partAsBytes.length);
            }

            for (byte b : partAsBytes) {
                bytes.add(b);
            }
        }

        System.out.println("Bytes size: " + bytes.size());

        return convertBytes(bytes);
    }
}
