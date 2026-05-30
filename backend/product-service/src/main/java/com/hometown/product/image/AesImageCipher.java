package com.hometown.product.image;

import com.hometown.common.web.ApiException;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Component
public class AesImageCipher {

    private static final int IV_LEN = 12;
    private static final int TAG_LEN = 128;

    private final SecretKey key;
    private final SecureRandom rng = new SecureRandom();

    public AesImageCipher(ImageProperties props) {
        byte[] raw = Base64.getDecoder().decode(props.getKeyBase64());
        this.key = new SecretKeySpec(raw, "AES");
    }

    public byte[] encrypt(byte[] plain) {
        try {
            byte[] iv = new byte[IV_LEN];
            rng.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LEN, iv));
            byte[] ciphertext = cipher.doFinal(plain);
            byte[] out = new byte[IV_LEN + ciphertext.length];
            System.arraycopy(iv, 0, out, 0, IV_LEN);
            System.arraycopy(ciphertext, 0, out, IV_LEN, ciphertext.length);
            return out;
        } catch (Exception e) {
            throw ApiException.badRequest("Encryption failed: " + e.getMessage());
        }
    }

    public byte[] decrypt(byte[] stored) {
        try {
            byte[] iv = Arrays.copyOfRange(stored, 0, IV_LEN);
            byte[] ciphertext = Arrays.copyOfRange(stored, IV_LEN, stored.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LEN, iv));
            return cipher.doFinal(ciphertext);
        } catch (Exception e) {
            throw ApiException.badRequest("Decryption failed: " + e.getMessage());
        }
    }
}
