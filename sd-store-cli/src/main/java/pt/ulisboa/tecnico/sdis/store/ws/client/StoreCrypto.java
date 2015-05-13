package pt.ulisboa.tecnico.sdis.store.ws.client;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import java.math.BigInteger;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class StoreCrypto {
    static String plainText = "1234567890123\0\0\0"; /*Note null padding*/
    static String IV = new BigInteger(130, plainText.getBytes()).toString(32);
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

    public byte[] encrypt(String plainText, String encryptionKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8")));
        return cipher.doFinal(plainText.getBytes("UTF-8"));
    }

    public String decrypt(byte[] cipherText, String encryptionKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8")));
        return new String(cipher.doFinal(cipherText), "UTF-8");
    }

    public String mac(byte[] contents) {
        try {
            //gerar digest para o contents
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(contents);
            byte[] digest = messageDigest.digest();

            //System.out.println("Digest_store:"+printHexBinary(digest));

            //concatenar digest e contents (digest no inicio)
            byte[] contentsDigest = new byte[contents.length + digest.length];
            System.arraycopy(digest, 0, contentsDigest, 0, digest.length);
            System.arraycopy(contents, 0, contentsDigest, digest.length, contents.length);

            return contentsDigest.toString();

        } catch (NoSuchAlgorithmException e) {
            System.out.println("ERROR: " + e.getMessage());
            return null;
        }
    }

    public String generateKCS() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        Key key = keyGen.generateKey();

        System.out.println("Plain: " + new String(key.getEncoded()));
        System.out.println("Encoded: " + printBase64Binary(key.getEncoded()));

        return printBase64Binary(key.getEncoded());
    }
}
