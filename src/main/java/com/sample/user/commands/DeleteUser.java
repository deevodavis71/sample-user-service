package com.sample.user.commands;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

import lombok.Value;

@Value
public class DeleteUser {
	@TargetAggregateIdentifier
	private String id;
}
