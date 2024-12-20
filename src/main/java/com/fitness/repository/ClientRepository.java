package com.fitness.repository;

import com.fitness.entity.Client;
import com.fitness.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    boolean existsByUser(User user);
    boolean existsByEmail(String email);

    Optional<Client> findByUser_Email(String email);

    // Добавьте этот метод для поиска клиента по username
    Optional<Client> findByUser_Username(String username);
    Optional<Client> findByUser(User user);


    @Modifying
    @Query("UPDATE Client c SET c.firstName = :firstName, c.lastName = :lastName, c.email = :email, c.phoneNumber = :phoneNumber, c.user = :user WHERE c.id = :id")
    int updateClient(Long id, String firstName, String lastName, String email, String phoneNumber, com.fitness.entity.User user);
}