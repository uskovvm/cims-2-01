package com.carddex.sims2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carddex.sims2.security.model.User;

/**
 * Created by
 */
public interface UserRepository extends JpaRepository<User, Long> {

	User findByUsername(String username);

}
