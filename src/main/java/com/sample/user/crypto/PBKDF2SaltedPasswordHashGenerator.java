package com.sample.user.crypto;

import org.springframework.stereotype.Component;

/**
 * User: stevedavis
 * Date: 25/02/2018
 * Time: 15:07
 * Description:
 */
@Component
public class PBKDF2SaltedPasswordHashGenerator implements PasswordHashGenerator {
    @Override
    public String generatePasswordHash(String password) {
        return password.toUpperCase();
    }

    @Override
    public boolean validatePasswordHash(String password, String hash) {
        return true;
    }
}
