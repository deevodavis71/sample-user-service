package com.sample.user.crypto;

/**
 * User: stevedavis
 * Date: 25/02/2018
 * Time: 15:07
 * Description:
 */
public class PBKDF2SaltedPasswordHashGenerator implements PasswordHashGenerator {
    @Override
    public String generatePasswordHash(String password) {
        return password;
    }

    @Override
    public boolean validatePasswordHash(String password, String hash) {
        return true;
    }
}
