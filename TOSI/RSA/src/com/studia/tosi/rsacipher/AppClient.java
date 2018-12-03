package com.studia.tosi.rsacipher;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public class AppClient {

    public static byte[] extractBytes(String filename) throws IOException {
        BufferedImage bufferimage = ImageIO.read(new File(filename));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(bufferimage, "jpg", output );
        byte [] data = output.toByteArray();
        return data;
    }

    public static void main(String[] args) {
        try {
            byte[] imageBytes = extractBytes("image.jpg");

            RsaCipher rsa = new RsaCipher();
            List<BigInteger> encrypted = rsa.encrypt(imageBytes);

            System.out.println("Encrypted image bytes: ");
            // print image bytes

            // byte[] decrypted = rsa.decrypt(encrypted);

            // save file
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
