package com.studia.tosi.cryptooverview.asymmetric;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RSACipher
{
    private static final int NUMBER_OF_BITS_IN_KEY = 512;

    private SecureRandom random;
    private RSAEngine rsaEngine;
    private AsymmetricCipherKeyPair keyPair;

    public RSACipher()
    {
        random = new SecureRandom();
        rsaEngine = new RSAEngine();

        createNewKey();
    }

    public void createNewKey()
    {
        //publicExponent, random, strength, certainty, random
        //certainty: the probability of the generated number not being prime is smaller than 2^−certainty
        RSAKeyGenerationParameters parameters = new RSAKeyGenerationParameters(BigInteger.probablePrime(NUMBER_OF_BITS_IN_KEY, random), random, 1024, 75);
        RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();
        keyPairGenerator.init(parameters);
        keyPair = keyPairGenerator.generateKeyPair();
    }

    public byte[] encrypt(byte[] message)
    {
        rsaEngine.init(true, keyPair.getPublic()); //true for encryption
        return rsaEngine.processBlock(message, 0, message.length);
    }

    public byte[] decrypt(byte[] message)
    {
        rsaEngine.init(false, keyPair.getPrivate()); //false for decryption
        return rsaEngine.processBlock(message, 0, message.length);
    }

    public AsymmetricKeyParameter getPublicKey()
    {
        return keyPair.getPublic();
    }

    public AsymmetricKeyParameter getPrivateKey()
    {
        return keyPair.getPrivate();
    }
}
