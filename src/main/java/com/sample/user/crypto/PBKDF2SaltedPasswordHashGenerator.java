package com.sample.user.crypto;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.springframework.stereotype.Component;

@Component
public class PBKDF2SaltedPasswordHashGenerator implements SaltedPasswordHashGenerator {

    final static int ITERATIONS = 5000;
    final static String HASH_ALGORITHM = "PBKDF2WithHmacSHA1";
    final static int KEYLENGTH = 512;

    @Override
    public String generatePasswordHash(String password) {
        try {
            return generatePasswordHash(password, getSalt());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String generatePasswordHash(String password, byte[] salt) {
        try {
            char[] chars = password.toCharArray();

            PBEKeySpec spec = new PBEKeySpec(chars, salt, ITERATIONS, KEYLENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(HASH_ALGORITHM);
            byte[] hash = skf.generateSecret(spec).getEncoded();

            String salt_str = toHex(salt);
            String hash_str = toHex(hash);

            return salt_str + ":" + hash_str;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean validatePasswordHash(String password, String stored) {

        // salt:hash
        String[] parts = stored.split(":");
        byte[] saltBytes = fromHex(parts[0]);
        byte[] hashBytes = fromHex(parts[1]);

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, ITERATIONS, hashBytes.length * 8);

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(HASH_ALGORITHM);
            byte[] testHash = skf.generateSecret(spec).getEncoded();

            int diff = hashBytes.length ^ testHash.length;

            for (int i = 0; i < hashBytes.length && i < testHash.length; i++) {
                diff |= hashBytes[i] ^ testHash[i];
            }

            return diff == 0;
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            //Not going to force callers to catch the checked exception because there's nothing they can do about it. This is a JVM issue.
            throw new RuntimeException(e);
        }
    }

    private byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    private byte[] fromHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

}
