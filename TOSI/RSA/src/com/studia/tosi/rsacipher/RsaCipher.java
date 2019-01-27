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
    private int bytesLength;

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

        int size = 0;

        for(int i = 0; i < devidedByteArray.size(); i++) {
            System.out.println("i = " + i + ", el size = " + devidedByteArray.get(i).length);
            size += devidedByteArray.get(i).length;
        }

        for (byte[] part : devidedByteArray) {
            bigIntegers.add(encryptPart(part));
        }

        System.out.println("Size: " + size);

        return bigIntegers;
    }

//    private static List<byte[]> divideByteArray(byte[] source, int chunksize) {
//        List<byte[]> result = new ArrayList<>();
//        int i,j;
//
//        for (i = 0, j = source.length; i <= j; i += chunksize) {
//            result.add(Arrays.copyOfRange(source, i, i + chunksize));
//        }
//
//        return result;
//    }
    private static List<byte[]> divideByteArray(byte[] source, int chunksize) {
        List<byte[]> result = new ArrayList<>();
        int start = 0;

        while (start < source.length) {
            if (source.length - chunksize < start) {
                System.out.println("LAST CHUNK");
                byte[] last = new byte[source.length - start];

                for(int i = start, j = 0; i < source.length; i++) {
                    System.out.println(j + ": " + source[i]);
                    last[j] = source[i];
                    j++;
                }
//                System.out.println(last.length);
                result.add(last);
            } else {
                // System.out.println(start);
                int end = Math.min(source.length, start + chunksize);
                result.add(Arrays.copyOfRange(source, start, end));
            }
            start += chunksize;
        }

        System.out.println("Last element of array length: " + result.get(result.size()-1).length);
        System.out.println("List of byte array size: " + result.size());

//        while (start < source.length) {
//            int end = Math.min(source.length, start + chunksize);
//            result.add(Arrays.copyOfRange(source, start, end));
//            start += chunksize;
//        }
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

        System.out.println("image bytes length: " + imageBytes.length);

        this.bytesLength = imageBytes.length;

        return encryptImageBytes(imageBytes);
    }


    private byte[] convertBytes(List<Byte> bytes) {
        byte[] ret = new byte[bytes.size()];
        for (int index = 0; index < bytes.size(); index++) {
            ret[index] = bytes.get(index);
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

        byte[] ret = new byte[bytes.length];

        for(int i = 0; i < bytes.length; i++) {
//             System.out.println(i);
            ret[i] = bytes[i];
        }

        System.out.println("Ret array size: " + ret.length);

        return ret;
    }



    public byte[] decrypt (List<BigInteger> message) {
        int msgSize = 0;
        int encryptedSize = 0;

        List<BigInteger> encrypted = new ArrayList<BigInteger>();

        // encrypt big integers
        for (BigInteger part : message) {
            byte[] msgPartAsBytes = BigIntegers.asUnsignedByteArray(part);
            msgSize += msgPartAsBytes.length;
            encrypted.add(decryptPart(part));

        }

        for(BigInteger part : encrypted) {
            byte[] partAsBytes = BigIntegers.asUnsignedByteArray(part);
            encryptedSize += partAsBytes.length;
        };

        System.out.println("message size: " + msgSize);
        System.out.println("encrypted size: " + encryptedSize);

        // List<BigInteger> encrypted = new ArrayList<>();

        // encrypt big integers
//        for (BigInteger part : message) {
//            encrypted.add(decryptPart(part));
//        }

        List <Byte> bytes = new ArrayList<>();

        for (int i = 0; i < encrypted.size(); i++) {
            BigInteger part = encrypted.get(i);
            byte[] partAsBytes = BigIntegers.asUnsignedByteArray(part);

//            if (partAsBytes.length < this.size && this.isLastPart(i, encrypted)) {
//                System.out.println("Last, i = " + i + ", el size = " + partAsBytes.length);
////                partAsBytes = removeAdditionalBytes(partAsBytes);
//            } else {
//                System.out.println("i = " + i + ", el size = " + partAsBytes.length);
//            }

            for (byte b : partAsBytes) {
                // System.out.println(b);
                bytes.add(b);
            }
        }

        byte[] b1 = convertBytes(bytes);

        System.out.println("Bytes size: " + b1.length);

        return b1;
    }
}
