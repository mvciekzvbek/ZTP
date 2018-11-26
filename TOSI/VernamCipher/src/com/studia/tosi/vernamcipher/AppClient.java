package com.studia.tosi.vernamcipher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AppClient {
    public static String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

    public static void main(String[] args) {
        String message = "";
        try {
            message = readFile("test.txt");
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println(message);

        VernamCipher vc = new VernamCipher(message);
        byte[] cypher = vc.encryptData();

        System.out.println(new String(cypher));
        String msg = vc.decryptData(cypher);
        System.out.println(msg);
    }
}
