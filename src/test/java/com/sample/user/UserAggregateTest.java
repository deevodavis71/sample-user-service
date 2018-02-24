package com.sample.user;

import static java.util.UUID.randomUUID;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.sample.user.UserAggregate;
import com.sample.user.commands.AuthenticateUser;
import com.sample.user.commands.ChangePassword;
import com.sample.user.commands.CreateUser;
import com.sample.user.commands.DeleteUser;
import com.sample.user.commands.UserAlreadyExistsException;
import com.sample.user.crypto.MockPasswordHashGenerator;
import com.sample.user.events.PasswordChanged;
import com.sample.user.events.UserAuthenticated;
import com.sample.user.events.UserAuthenticationFailed;
import com.sample.user.events.UserCreated;
import com.sample.user.events.UserDeleted;
import com.sample.user.query.User;
import com.sample.user.query.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserAggregateTest {

	private FixtureConfiguration<UserAggregate> fixture;

	@Autowired
	private UserRepository userRepository;
	
	@Before
	public void setUp() {
		fixture = new AggregateTestFixture<UserAggregate>(UserAggregate.class)
					.registerInjectableResource(new MockPasswordHashGenerator())
					.registerInjectableResource(userRepository);
	}

	@Test
	public void testCreateUser() {
		String id = randomUUID().toString();
		
		fixture
			.givenNoPriorActivity()
			.when(new CreateUser(id, "user@sample.com", "password"))
			.expectEvents(new UserCreated(id, "user@sample.com", "password"));
	}
	
	@Test
	public void testUserAlreadyExistsException() {
		User user = userRepository.save(new User(randomUUID().toString(), "user@sample.com"));
		
		fixture
			.givenNoPriorActivity()
			.when(new CreateUser(randomUUID().toString(), "user@sample.com", "password"))
			.expectException(UserAlreadyExistsException.class);
		
		userRepository.delete(user);
	}
	
	@Test
	public void testUserAuthenticated() {
		String id = randomUUID().toString();
		
		fixture
			.given(new UserCreated(id, "user@sample.com", "password"))
			.when(new AuthenticateUser(id, "password"))
			.expectEvents(new UserAuthenticated(id));
	}

	@Test
	public void testUserAuthenticationFailed() {
		String id = randomUUID().toString();
		
		fixture
			.given(new UserCreated(id, "user@sample.com", "password"))
			.when(new AuthenticateUser(id, "p@ssword"))
			.expectEvents(new UserAuthenticationFailed(id));
	}
	
	@Test
	public void testDeleteUser() {
		String id = randomUUID().toString();
		
		fixture
			.given(new UserCreated(id, "user@sample.com", "password"))
			.when(new DeleteUser(id))
			.expectEvents(new UserDeleted(id));
	}
	
	@Test
	public void testChangePassword() {
		String id = randomUUID().toString();

		fixture
			.given(new UserCreated(id, "user@sample.com", "password"))
			.when(new ChangePassword(id, "p@ssword"))
			.expectEvents(new PasswordChanged(id, "p@ssword"));
	}
}
