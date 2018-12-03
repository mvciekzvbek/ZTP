package com.studia.tosi.rsacipher;

import java.math.BigInteger;

public class PrivateKey {
    public BigInteger n;
    public BigInteger d;

    public PrivateKey (BigInteger n, BigInteger d) {
        this.n = n;
        this.d = d;
    }
}
