package com.sample.user.events;

import lombok.Value;

@Value
public class PasswordChanged {
	private String id, passwordHash;
}
