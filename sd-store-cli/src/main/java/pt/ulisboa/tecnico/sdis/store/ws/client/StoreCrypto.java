package pt.ulisboa.tecnico.sdis.store.ws.client;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class StoreCrypto {
    static String IV = "AAAAAAAAAAAAAAAA";
    static String plainText = "1234567890123\0\0\0"; /*Note null padding*/
    static String encryptionKey = "0123456789abcdef";
    static String contents = "abc";

/*
    public static void main(String[] args) {
        try {

            System.out.println("==Java==");
            System.out.println("plain:   " + plaintext);

            byte[] cipher = encrypt(plaintext, encryptionKey);

            System.out.print("cipher:  ");
            for (int i = 0; i < cipher.length; i++)
                System.out.print(new Integer(cipher[i]) + " ");
            System.out.println("");

            String decrypted = decrypt(cipher, encryptionKey);

            System.out.println("decrypt: " + decrypted);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static byte[] encrypt(String plainText, String encryptionKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8")));
        return cipher.doFinal(plainText.getBytes("UTF-8"));
    }

    public static String decrypt(byte[] cipherText, String encryptionKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8")));
        return new String(cipher.doFinal(cipherText), "UTF-8");
    }

    public static void mac(byte[] contents) {
        MessageDigest md;

        try {
            md = MessageDigest.getInstance("SHA-1");

            md.update(contents);
            byte[] mb = md.digest();
            String out = "";
            for (int i = 0; i < mb.length; i++) {
                byte temp = mb[i];
                String s = Integer.toHexString(new Byte(temp));
                while (s.length() < 2) {
                    s = "0" + s;
                }
                s = s.substring(s.length() - 2);
                out += s;
            }
            System.out.println(out);

        } catch (NoSuchAlgorithmException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

}
