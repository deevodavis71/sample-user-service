package com.sample.user;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;
import static org.axonframework.commandhandling.model.AggregateLifecycle.markDeleted;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import com.sample.user.commands.AuthenticateUser;
import com.sample.user.commands.ChangePassword;
import com.sample.user.commands.CreateUser;
import com.sample.user.commands.DeleteUser;
import com.sample.user.commands.UserAlreadyExistsException;
import com.sample.user.crypto.PasswordHashGenerator;
import com.sample.user.events.PasswordChanged;
import com.sample.user.events.UserAuthenticated;
import com.sample.user.events.UserAuthenticationFailed;
import com.sample.user.events.UserCreated;
import com.sample.user.events.UserDeleted;
import com.sample.user.query.UserRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Aggregate
@Slf4j
public class UserAggregate {

    @AggregateIdentifier
    private String id;

    private String
            email,
            passwordHash;

    public UserAggregate() {
    }

    @CommandHandler
    public UserAggregate(CreateUser cmd, PasswordHashGenerator passwordHashGenerator, UserRepository userRepository) throws UserAlreadyExistsException {

        if (userRepository.findByEmailIgnoreCase(cmd.getEmail()) == null) {
            apply(new UserCreated(cmd.getId(), cmd.getEmail(), passwordHashGenerator.generatePasswordHash(cmd.getPassword())));
        } else {
            throw new UserAlreadyExistsException();
        }
    }

    @EventSourcingHandler
    public void userCreated(UserCreated event) {

        log.debug("AGG-EVENT> Constructing new User: {}", event.getEmail());

        setId(event.getId());
        setEmail(event.getEmail());
        setPasswordHash(event.getPasswordHash());
    }

    @CommandHandler
    public void authenticateUser(AuthenticateUser cmd, PasswordHashGenerator passwordHashGenerator) {
        if (passwordHashGenerator.validatePasswordHash(cmd.getPassword(), getPasswordHash())) {
            apply(new UserAuthenticated(getId()));
        } else {
            apply(new UserAuthenticationFailed(getId()));
        }
    }

    @CommandHandler
    public void changePassword(ChangePassword cmd, PasswordHashGenerator passwordHashGenerator) {
        apply(new PasswordChanged(getId(), passwordHashGenerator.generatePasswordHash(cmd.getPassword())));
    }

    @EventSourcingHandler
    public void passwordChanged(PasswordChanged event) {

        log.debug("AGG-EVENT> Changing password for User: {} to {}", event.getId(), event.getPasswordHash());

        setPasswordHash(event.getPasswordHash());
    }

    @CommandHandler
    public void deleteUser(DeleteUser cmd) {
        apply(new UserDeleted(getId()));
    }

    @EventSourcingHandler
    public void userDeleted(UserDeleted event) {

        log.debug("AGG-EVENT> Deleting User: {}", event.getId());

        markDeleted();
    }
}
