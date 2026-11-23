package com.fakultas.fasilitas.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

    public static String hashPassword(String plainPassword) {
        if (plainPassword == null) {
            return "";
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritma SHA-256 tidak tersedia", e);
        }
    }

    public static boolean isSha256Hex(String value) {
        return value != null && value.matches("^[0-9a-fA-F]{64}$");
    }

    public static boolean verifyPassword(String plainInput, String storedPassword) {
        if (plainInput == null || storedPassword == null) {
            return false;
        }
        if (isSha256Hex(storedPassword)) {
            return hashPassword(plainInput).equalsIgnoreCase(storedPassword);
        }
        return plainInput.equals(storedPassword);
    }
}