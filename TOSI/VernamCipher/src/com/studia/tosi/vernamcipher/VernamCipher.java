package com.studia.tosi.vernamcipher;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VernamCipher {
    private String message;
    private String messageBinary;
    private boolean logOn = true;
    private String keys = "";

    public VernamCipher (String message) {
        this.log("VernamCipher: constructor");
        this.message = message;
        this.messageBinary = this.toBinary(message, 8);
        this.log(Integer.toString(this.messageBinary.length()));
    }

    private void log (String message) {
        if (this.logOn) {
            System.out.println(message);
        }
    }

    private String toBinary(String str) {
        String result = "";
        char[] messChar = str.toCharArray();

        for (int i = 0; i < messChar.length; i++) {
            result += Integer.toBinaryString(messChar[i]);
        }

        this.log(result);

        return result;
    }

    private String toBinary(String str, int bits) {
        String result = "";
        String tmpStr;
        int tmpInt;
        char[] messChar = str.toCharArray();

        for (int i = 0; i < messChar.length; i++) {
            tmpStr = Integer.toBinaryString(messChar[i]);
            tmpInt = tmpStr.length();
            if(tmpInt != bits) {
                tmpInt = bits - tmpInt;
                if (tmpInt == bits) {
                    result += tmpStr;
                } else if (tmpInt > 0) {
                    for (int j = 0; j < tmpInt; j++) {
                        result += "0";
                    }
                    result += tmpStr;
                } else {
                    System.err.println("argument 'bits' is too small");
                }
            } else {
                result += tmpStr;
            }
        }

        this.log(result);
        return result;
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
            p = new BigInteger(512, 1, rnd);
            q = new BigInteger(512, 1, rnd);
        } while (p.mod(four).equals(three) && q.mod(four).equals(three));

        primes[0] = p;
        primes[1] = q;

        return primes;
    }

    private String executeBlumBlumShub () {
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
        String k = "";

        x.add(x0);

        for (int i = 1; i <= this.messageBinary.length(); i++) {
            x.add(((x.get(i - 1)).pow(2)).mod(n));
            k += x.get(i).mod(two);
        }

        return k;
    }

    public String encryptData () {
        this.log("VernamCipher: encryptData");

        this.keys = this.executeBlumBlumShub();
        String cypher = "";
        this.log(this.keys);
        this.log(this.messageBinary);

        for(int i = 0; i < this.keys.length(); i++) {
            byte key = (byte) this.keys.charAt(i);
            byte msgEl = (byte) this.messageBinary.charAt(i);

            System.out.println((char) key);
            System.out.println((char) msgEl);

            cypher += Integer.toString((key ^ msgEl) % 2);
        }

        return cypher;
    }

    public String decryptData(String text) {
        String msg = "";

        for(int i = 0; i < this.keys.length(); i++) {
            byte key = (byte) this.keys.charAt(i);
            byte encryptedEl = (byte) text.charAt(i);

            System.out.println((char) key);
            System.out.println((char) encryptedEl);

            int msgEl = (key ^ encryptedEl) % 2;

            this.log(Integer.toString(msgEl));

            msg += Integer.toString(msgEl);
        }

        String output = "";
        for(int i = 0; i <= msg.length() - 8; i+=8)
        {
            int k = Integer.parseInt(msg.substring(i, i+8), 2);
            output += (char) k;
        }
        return output;
    }

}