package com.sample.user.events;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class UserCreated {
	private String id, email, passwordHash;
}
