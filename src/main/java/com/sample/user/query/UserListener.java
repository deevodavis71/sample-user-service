package com.sample.user.query;

import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sample.user.events.UserCreated;
import com.sample.user.events.UserDeleted;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UserListener {

	@Autowired
	private UserRepository userRepository;

	@EventHandler
	public void userCreated(UserCreated event) {

		log.debug("LIST-EVENT> User Created : {} {}", event.getId(), event.getEmail());

		userRepository.save(new User(event.getId(), event.getEmail()));
	}
	
	@EventHandler
	public void userDeleted(UserDeleted event) {

		log.debug("LIST-EVENT> User Deleted : {} {}", event.getId());

		userRepository.delete(event.getId());
	}
}
