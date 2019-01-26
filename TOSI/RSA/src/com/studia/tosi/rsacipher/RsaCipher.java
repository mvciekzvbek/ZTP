package com.studia.tosi.rsacipher;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bouncycastle.util.BigIntegers;

import static java.util.Arrays.copyOfRange;

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

    private BigInteger encryptPart(BigInteger part) {
        return part.modPow(this.publicKey.e, this.publicKey.n);
    }

    private BigInteger decryptPart(BigInteger part) {
        return part.modPow(this.privateKey.d, this.privateKey.n);
    }

    private List<BigInteger> createDataChunks(byte[] data)
    {
        List<BigInteger> chunks = new ArrayList<>();
        byte[] chunk = null;
        int chunksCount = 0;

        for (byte item : data)
        {
            if (chunk == null)
            {
                chunk = new byte[255];
            }

            chunk[chunksCount++] = item;
            if (chunksCount != 255)
            {
                continue;
            }

            chunks.add(new BigInteger(1, chunk));

            chunk = null;
            chunksCount = 0;
        }

        if (chunk != null && chunksCount > 0)
        {
            byte[] lastChunk = copyOfRange(chunk, 0, chunksCount + 1);
            chunks.add(new BigInteger(1, lastChunk));
        }

        return chunks;
    }

    private List<BigInteger> encryptImageBytes(byte[] imageBytes) {
        // List<byte[]> devidedByteArray = divideByteArray(imageBytes, this.size);
        List<BigInteger> devidedByteArray = createDataChunks(imageBytes);

        List<BigInteger> bigIntegers = new ArrayList<>();

        for(BigInteger el : devidedByteArray) {
            bigIntegers.add(encryptPart(el));
        }

        return bigIntegers;
    }
//
//    private static List<byte[]> divideByteArray(byte[] source, int chunksize) {
//        List<byte[]> result = new ArrayList<>();
//        int start = 0;
//
//        while (start < source.length) {
//            if (source.length - chunksize < start) {
//                System.out.println("LAST CHUNK");
//                byte[] last = new byte[source.length - start];
//
//                for(int i = start, j = 0; i < source.length; i++) {
//                    System.out.println(j + ": " + source[i]);
//                    last[j] = source[i];
//                    j++;
//                }
//                result.add(last);
//            } else {
//                int end = Math.min(source.length, start + chunksize);
//                result.add(Arrays.copyOfRange(source, start, end));
//            }
//            start += chunksize;
//        }
//
//        System.out.println("Last element of array length: " + result.get(result.size()-1).length);
//        System.out.println("List of byte array size: " + result.size());
//
//        System.out.println("DEVIDE START");
//        for (byte[] bt : result) {
//            System.out.println(bt.length);
//        }
//        System.out.println("DEVIDE END");
//        return result;
//    }

    public List<BigInteger> encrypt (byte[] bytes) {
        byte[] imageBytes = bytes;

        BigInteger[] primes = generatePrimes();
        BigInteger n = calculateN(primes);
        BigInteger fi = calculateFi(primes);
        BigInteger euler = calculateEuler(fi);

        this.publicKey = createPublicKey(n, euler);
        this.privateKey = createPrivateKey(euler, fi, n);

        System.out.println("image bytes length: " + imageBytes.length);

        return encryptImageBytes(imageBytes);
    }


    private byte[] convertBytes(List<Byte> bytes) {
        byte[] ret = new byte[bytes.size()];
        for (int index = 0; index < bytes.size(); index++) {
            ret[index] = bytes.get(index);
        }
        return ret;
    }

    private boolean isLastChunk(int index, List<BigInteger> decrypted) {
        return index == decrypted.size() - 1;
    }

    private List<Byte> addMissingBytes(List<Byte> chunkBytes) {
        int missing = 255 - chunkBytes.size();

        List<Byte> padding = new ArrayList<>();

        for (int i = 0; i < missing; i++) {
            chunkBytes.add(new Byte("0"));
        }

        return chunkBytes;
    }

    private List<Byte> convertToByteList(byte[] bytes)
    {
        List<Byte> result = new ArrayList<>();

        for (byte b : bytes)
        {
            result.add(b);
        }

        return result;
    }

    public byte[] decrypt (List<BigInteger> message) {

        List<BigInteger> decrypted = new ArrayList<BigInteger>();

        for (BigInteger part : message) {
            decrypted.add(decryptPart(part));
        }

        List<Byte> decryptedBytes = new ArrayList<>();

        for (int i = 0; i < decrypted.size(); i++) {
            BigInteger chunk = decrypted.get(i);
            List<Byte> chunkBytes = convertToByteList(BigIntegers.asUnsignedByteArray(chunk));

            if (chunkBytes.size() < 255 && !isLastChunk(i, decrypted)) {
                chunkBytes = addMissingBytes(chunkBytes);
            }

            for (int j = 0; j < chunkBytes.size(); j++) {
                decryptedBytes.add(chunkBytes.get(j));
            }
        }

        return convertBytes(decryptedBytes);
    }
}
