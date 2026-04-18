package com.workintech.twitter.repository;

import com.workintech.twitter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //username e göre kullanıcıyı buluyorum (olabilir de olmayabilir diye Optional)
    Optional<User> findByUsername(String username);

    //emaile göre kullanıcıyı buluyorum (olabilir de olmayabilir diye Optional)
    Optional<User> findByEmail(String email);
}
