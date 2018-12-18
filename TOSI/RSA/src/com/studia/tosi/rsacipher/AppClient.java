package com.studia.tosi.rsacipher;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.util.List;

public class AppClient {

    public static byte[] extractBytes(String filename) throws IOException {
        BufferedImage bufferimage = ImageIO.read(new File(filename));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(bufferimage, "jpg", output);

        return output.toByteArray();
    }


    public static void createImageFromBytes(byte[] imageBytes) throws IOException {
        try (FileOutputStream fos = new FileOutputStream("output.jpg")) {
            fos.write(imageBytes);
        }
    }

    public static void main(String[] args) {
        try {
            byte[] imageBytes = extractBytes("image.jpg");

            RsaCipher rsa = new RsaCipher();
            List<BigInteger> encrypted = rsa.encrypt(imageBytes);

            byte[] decrypted = rsa.decrypt(encrypted);

            createImageFromBytes(decrypted);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
