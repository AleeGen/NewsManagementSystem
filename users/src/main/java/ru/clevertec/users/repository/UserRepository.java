package ru.clevertec.users.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.clevertec.users.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Cacheable(cacheNames = "cache", key = "#username")
    Optional<User> findByUsername(String username);

}