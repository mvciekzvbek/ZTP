package com.studia.tosi.vernamcipher;

import java.util.Scanner;

public class AppClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please type your message below:");
        String message = scanner.nextLine();
        System.out.println(message);

        VernamCipher vc = new VernamCipher(message);
        String cypher = vc.encryptData();
        System.out.println(cypher);
        String msg = vc.decryptData(cypher);
        System.out.println(msg);
    }
}
