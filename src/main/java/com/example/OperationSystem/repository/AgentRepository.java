package com.example.OperationSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.OperationSystem.entity.Agent;

import java.util.Optional;

public interface AgentRepository extends JpaRepository<Agent, Long> {

    Optional<Agent> findByEmail(String email);

    boolean existsByEmail(String email);
}
