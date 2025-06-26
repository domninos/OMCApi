package net.omc.util;

import net.omc.OMCPlugin;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class Cryptography {
    private static final String ALGORITHM = "AES";

    private final OMCPlugin plugin;

    public Cryptography(OMCPlugin plugin) {
        this.plugin = plugin;
    }

    // AES with random IV (CBC)
    public String decrypt(String encryptedText, String key) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedText);
            byte[] iv = new byte[16];
            byte[] encrypted = new byte[combined.length - 16];

            System.arraycopy(combined, 0, iv, 0, 16);
            System.arraycopy(combined, 16, encrypted, 0, encrypted.length);

            SecretKeySpec secretKey = new SecretKeySpec(getKeyBytes(key), ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            byte[] original = cipher.doFinal(encrypted);

            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception e) {
            plugin.error("Something went wrong encrypting the license.", e);
            return "NULL";
        }
    }

    // AES with random IV (CBC)
    public String encrypt(String plainText, String key) {
        try {
            byte[] keyBytes = getKeyBytes(key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            plugin.error("Something went wrong decrypting license.", e);
            return "NULL";
        }
    }

    private byte[] getKeyBytes(String key) {
        byte[] keyByte = new byte[16]; // AES-128 (16 bytes)
        byte[] input = key.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(input, 0, keyByte, 0, Math.min(input.length, 16));
        return keyByte;
    }
}
