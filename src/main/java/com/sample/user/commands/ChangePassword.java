package com.sample.user.commands;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import lombok.Value;

@Value
public class ChangePassword {
	@TargetAggregateIdentifier
	private String id;
	private String password;
}
