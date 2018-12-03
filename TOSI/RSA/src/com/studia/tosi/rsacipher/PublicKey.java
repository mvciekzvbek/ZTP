package com.studia.tosi.rsacipher;

import java.math.BigInteger;

public class PublicKey {
    public BigInteger n;
    public BigInteger e;

    public PublicKey (BigInteger n, BigInteger e) {
        this.n = n;
        this.e = e;
    }
}
