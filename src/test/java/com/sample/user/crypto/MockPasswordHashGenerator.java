package com.sample.user.crypto;

import com.sample.user.crypto.PasswordHashGenerator;

public class MockPasswordHashGenerator implements PasswordHashGenerator {

	@Override
	public String generatePasswordHash(String password) {
		return password;
	}

	@Override
	public boolean validatePasswordHash(String password, String hash) {
		return password.equals(hash);
	}

}
