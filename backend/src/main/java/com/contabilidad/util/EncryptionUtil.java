package com.contabilidad.util;

import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Component
public class EncryptionUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    private static final int KEY_LENGTH = 256;
    
    // In production, this should be stored securely (e.g., environment variable, vault)
    private static final String SECRET_KEY = "YourSecretKeyHere12345678901234567890123456789012";
    
    private final SecretKey secretKey;
    private final SecureRandom secureRandom;

    public EncryptionUtil() {
        this.secretKey = generateSecretKey();
        this.secureRandom = new SecureRandom();
    }

    /**
     * Encrypts sensitive data
     * @param plaintext The text to encrypt
     * @return Base64 encoded encrypted string
     */
    public String encrypt(String plaintext) {
        try {
            if (plaintext == null || plaintext.isEmpty()) {
                return plaintext;
            }

            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            byte[] encrypted = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, encrypted, 0, iv.length);
            System.arraycopy(cipherText, 0, encrypted, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    /**
     * Decrypts encrypted data
     * @param encryptedText Base64 encoded encrypted string
     * @return Decrypted plaintext
     */
    public String decrypt(String encryptedText) {
        try {
            if (encryptedText == null || encryptedText.isEmpty()) {
                return encryptedText;
            }

            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            byte[] iv = Arrays.copyOfRange(decoded, 0, GCM_IV_LENGTH);
            byte[] cipherText = Arrays.copyOfRange(decoded, GCM_IV_LENGTH, decoded.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] plaintext = cipher.doFinal(cipherText);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data", e);
        }
    }

    /**
     * Generates a secure hash for passwords
     * @param password The password to hash
     * @return Hashed password with salt
     */
    public String hashPassword(String password) {
        try {
            byte[] salt = new byte[16];
            secureRandom.nextBytes(salt);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Verifies a password against its hash
     * @param password The password to verify
     * @param hashedPassword The stored hash
     * @return true if password matches
     */
    public boolean verifyPassword(String password, String hashedPassword) {
        try {
            byte[] combined = Base64.getDecoder().decode(hashedPassword);
            byte[] salt = Arrays.copyOfRange(combined, 0, 16);
            byte[] storedHash = Arrays.copyOfRange(combined, 16, combined.length);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] inputHash = md.digest(password.getBytes(StandardCharsets.UTF_8));

            return Arrays.equals(storedHash, inputHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Generates a secure random token
     * @param length The length of the token
     * @return Base64 encoded token
     */
    public String generateSecureToken(int length) {
        byte[] token = new byte[length];
        secureRandom.nextBytes(token);
        return Base64.getEncoder().encodeToString(token);
    }

    /**
     * Masks sensitive data for display
     * @param data The data to mask
     * @param visibleChars Number of characters to show at the end
     * @return Masked data
     */
    public String maskSensitiveData(String data, int visibleChars) {
        if (data == null || data.length() <= visibleChars) {
            return data;
        }
        return "*".repeat(data.length() - visibleChars) + data.substring(data.length() - visibleChars);
    }

    /**
     * Validates if a string contains potentially dangerous content
     * @param input The string to validate
     * @return true if safe, false if potentially dangerous
     */
    public boolean isSafeInput(String input) {
        if (input == null) {
            return true;
        }

        String lowerInput = input.toLowerCase();
        
        // Check for SQL injection patterns
        String[] sqlPatterns = {
            "select", "insert", "update", "delete", "drop", "create", "alter", "exec", "union",
            "script", "javascript:", "vbscript:", "onload=", "onerror=", "eval(", "document.cookie"
        };

        for (String pattern : sqlPatterns) {
            if (lowerInput.contains(pattern)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Sanitizes input by removing dangerous characters
     * @param input The input to sanitize
     * @return Sanitized input
     */
    public String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }

        return input
            .replaceAll("<script[^>]*>.*?</script>", "")
            .replaceAll("<[^>]*>", "")
            .replaceAll("javascript:", "")
            .replaceAll("vbscript:", "")
            .replaceAll("onload=", "")
            .replaceAll("onerror=", "")
            .replaceAll("eval\\(", "")
            .replaceAll("document\\.cookie", "")
            .trim();
    }

    private SecretKey generateSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(KEY_LENGTH, secureRandom);
            return keyGen.generateKey();
        } catch (Exception e) {
            // Fallback to using the secret key string
            byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
            return new SecretKeySpec(keyBytes, "AES");
        }
    }
} 