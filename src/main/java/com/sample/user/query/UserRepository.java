package com.sample.user.query;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    public User findByEmailIgnoreCase(String email);

}
