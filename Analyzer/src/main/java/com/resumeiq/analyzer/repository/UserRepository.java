package com.resumeiq.analyzer.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.resumeiq.analyzer.model.User;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}