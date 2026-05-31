package com.example.OperationSystem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.OperationSystem.dto.request.CreateAgentRequest;
import com.example.OperationSystem.dto.response.AgentResponse;
import com.example.OperationSystem.entity.Agent;
import com.example.OperationSystem.entity.User;
import com.example.OperationSystem.exceptions.BusinessException;
import com.example.OperationSystem.exceptions.ResourceNotFoundException;
import com.example.OperationSystem.repository.AgentRepository;
import com.example.OperationSystem.repository.InquiryAgentRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgentService {
    private final AgentRepository agentrepository;
    private final InquiryAgentRepository inquiryAgentRepository;
    
    @CacheEvict(value = "agents", allEntries = true)
    public AgentResponse createAgent(CreateAgentRequest request, User currentUser) {

        if(agentrepository.existsByEmail(request.getEmail())){
            throw new BusinessException("Agent with email " + request.getEmail() + " already exists");
        }
        Agent agent = Agent.builder()
            .name(request.getName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .address(request.getAddress())
            .Source(request.getSource())
            .CompanyName(request.getCompanyName())
            .Country(request.getCountry())
            .build();
            
        return AgentResponse.from(agentrepository.save(agent));
    }

    @Cacheable("agents")
    public List<AgentResponse> getAllAgents(User currentUser) {
    
        return agentrepository.findAll()
            .stream()
            .map(AgentResponse::from)
            .toList();
    }

    public AgentResponse getAgentById(Long id, User currentUser) {
        
        return agentrepository.findById(id)
            .map(AgentResponse::from)
            .orElseThrow(() -> new ResourceNotFoundException("Agent not found with id: " + id));
    }

    @Transactional
    @CacheEvict(value = "agents", allEntries = true)
    public void deleteAgentById(Long id, User currentUser) {
        Agent agent = agentrepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Agent not found with id: " + id));
        inquiryAgentRepository.deleteByAgentId(id);
        agentrepository.delete(agent);
    }

    @CacheEvict(value = "agents", allEntries = true)
    public AgentResponse updateAgent(Long id, CreateAgentRequest request, User currentUser) {

    // Step 1 — Find existing agent or throw 404
        Agent agent = agentrepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found with id: " + id));

        // Step 2 — Update the existing agent's fields
        agent.setName(request.getName());
        agent.setEmail(request.getEmail());
        agent.setPhone(request.getPhone());
        agent.setAddress(request.getAddress());
        agent.setSource(request.getSource());
        agent.setCompanyName(request.getCompanyName());
        agent.setCountry(request.getCountry());

        // Step 3 — Save and return
        agentrepository.save(agent);
        return AgentResponse.from(agent);
    }

}

    
