package com.FAuth.proauth.repository;

import com.FAuth.proauth.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User,String> {
    boolean existsByEmail(String email);
}
