package com.sample.user.events;

import lombok.Value;

@Value
public class UserAuthenticationFailed {
	private String id;
}
