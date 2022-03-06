package com.mfpe.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mfpe.authentication.entity.AppUser;

/**
 * Repository Layer Interface for Authentication Microservice
 */
@Repository
public interface UserRepository extends JpaRepository<AppUser, String> {

}