package com.studia.tosi.vernamcipher;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public class VernamCipher {
    private boolean logOn = false;
    private BitSet key;
    private BitSet messageBits;

    public VernamCipher (String message) {
        this.log("VernamCipher: constructor");
        this.messageBits = this.toBits(message);
        this.key = this.executeBlumBlumShub();
    }

    private void log (String message) {
        if (this.logOn) {
            System.out.println(message);
        }
    }

    private BitSet toBits (String message) {
        byte[] bytes = message.getBytes();

        BitSet bs = BitSet.valueOf(bytes);

        return bs;
    }

    private BigInteger calculateN (BigInteger p, BigInteger q) {
        return p.multiply(q);
    }

    private BigInteger calculateS (BigInteger n) {
        Random rnd = new Random();
        BigInteger one = new BigInteger("1");
        BigInteger s;

        do {
            s = new BigInteger(n.bitLength(), rnd);
        } while (s.compareTo(n) > 0 && s.compareTo(one) >= 0 && (s.gcd(n)).equals(one));

        return s;
    }

    private BigInteger[] generatePrimes() {
        BigInteger[] primes = new BigInteger[2];
        BigInteger p, q, three, four;

        three = new BigInteger("3");
        four = new BigInteger("4");

        do {
            Random rnd = new Random();
            p = new BigInteger(512, 1000, rnd);
            q = new BigInteger(512, 1000, rnd);
        } while (p.mod(four).equals(three) && q.mod(four).equals(three));

        primes[0] = p;
        primes[1] = q;

        return primes;
    }

    private BitSet executeBlumBlumShub () {
        BigInteger[] primes = this.generatePrimes();
        BigInteger p = primes[0];
        BigInteger q = primes[1];

        BigInteger zero = new BigInteger("0");
        BigInteger two = new BigInteger("2");

        BigInteger n = calculateN(p,q);
        BigInteger s = calculateS(n);

        this.log("n: " + n.toString());
        this.log("s: " + s.toString());

        BigInteger x0 = (s.pow(2)).mod(n);

        this.log("x0: " + x0.toString());

        List<BigInteger> x = new ArrayList<BigInteger>();
        BitSet k = new BitSet();

        x.add(x0);

        for (int i = 1; i <= this.messageBits.length(); i++) {
            x.add(((x.get(i - 1)).pow(2)).mod(n));
            if (!x.get(i).mod(two).equals(zero)) {
                k.set(i);
            }
        }

        return k;
    }

    public byte[] encryptData() {
        BitSet key = (BitSet) this.key.clone();

        key.xor(this.messageBits);

        return key.toByteArray();
    }

    public String decryptData(byte[] bytes) {
        BitSet bs = BitSet.valueOf(bytes);
        BitSet key = (BitSet) this.key.clone();

        key.xor(bs);

        byte[] decodedBytes = key.toByteArray();

        return new String(decodedBytes);
    }
}