package com.sample.user.crypto;

public interface PasswordHashGenerator {
	public String generatePasswordHash(String password);
	boolean validatePasswordHash(String password, String hash);
}
