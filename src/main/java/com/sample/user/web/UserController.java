package com.sample.user.web;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sample.user.commands.CreateUser;
import com.sample.user.commands.DeleteUser;
import com.sample.user.crypto.PasswordHashGenerator;
import com.sample.user.query.User;
import com.sample.user.query.UserRepository;
import com.sample.user.snapshot.UserSnapshotter;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSnapshotter shotter;

    @Autowired
    private PasswordHashGenerator hashGen;

    @PostMapping
    public CompletableFuture<Object> createUser(@RequestBody Map<String, String> request) {

        log.debug("Creating User : {}", request.get("email"));

        return commandGateway.send(new CreateUser(
                Optional.ofNullable(request.get("id")).orElse(UUID.randomUUID().toString()),
                request.get("email"),
                request.get("password")));
    }

//	@PostMapping
//	public CompletableFuture<Object> authenticateUser(@RequestBody Map<String, String> request) {
//		User user = userRepository.findByEmail(request.get("email"));
//		
//		return commandGateway.send(new AuthenticateUser(user.getId(), request.get("password")));
//	}

    @GetMapping
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<Object> deleteUser(@PathVariable String id) throws UserNotFoundException {
        if (userRepository.exists(id)) {
            return commandGateway.send(new DeleteUser(id));
        } else {
            throw new UserNotFoundException();
        }
    }

    @PutMapping(path = "{id}/snapshot")
    public void snapshot(@PathVariable String id) {

        shotter.createSnapshot(id);

    }

    @GetMapping("/testPassword/{password}")
    public String testPasswordHashBeanIsAvailable(@PathVariable String password) {

        return hashGen.generatePasswordHash(password);

    }
}
