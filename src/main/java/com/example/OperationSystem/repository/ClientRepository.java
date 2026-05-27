package com.example.OperationSystem.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.OperationSystem.entity.Client;
import com.example.OperationSystem.entity.User;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> 
    {

    boolean existsByCompanyName(String companyName);
    
    List<Client> findByCreatedBy(User createdBy);
    Optional<Client> findByIdAndCreatedBy(Long id, User createdBy);


    
    }
