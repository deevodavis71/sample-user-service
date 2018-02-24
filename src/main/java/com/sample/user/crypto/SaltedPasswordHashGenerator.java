package com.sample.user.crypto;

public interface SaltedPasswordHashGenerator extends PasswordHashGenerator {
	public String generatePasswordHash(String password, byte[] salt);
}
