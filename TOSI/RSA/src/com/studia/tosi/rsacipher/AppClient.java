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

        byte[] bytes = output.toByteArray();

        System.out.println(bytes.length);

        return bytes;
    }


    public static void createImageFromBytes(byte[] imageBytes, String filename, String ext) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filename + "-output." + ext)) {
            fos.write(imageBytes);
        }
    }

    public static void main(String[] args) {
        try {

            String filenameWithExt = "pingwin.jpg";

            String filename = filenameWithExt.split("\\.")[0];
            String ext = filenameWithExt.split("\\.")[1];

            byte[] imageBytes = extractBytes(filenameWithExt);

            RsaCipher rsa = new RsaCipher();
            List<BigInteger> encrypted = rsa.encrypt(imageBytes);
            // System.out.println(encrypted.size());

            byte[] decrypted = rsa.decrypt(encrypted);
             System.out.println("AppClient.decrypted.length: " + decrypted.length);

            createImageFromBytes(decrypted, filename, ext);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
