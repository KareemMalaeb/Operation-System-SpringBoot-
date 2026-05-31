package com.example.OperationSystem.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.OperationSystem.enums.ContainerType;
import com.example.OperationSystem.enums.Encoterms;
import com.example.OperationSystem.enums.FreightType;

@Service
public class EnumService {

    @Cacheable("enums")
    public Map<String, List<Map<String, String>>> getEnums() {
        List<Map<String, String>> containerTypes = Arrays.stream(ContainerType.values())
                .map(e -> Map.of("value", e.name(), "label", e.getLabel()))
                .toList();

        List<Map<String, String>> freightTypes = Arrays.stream(FreightType.values())
                .map(e -> Map.of("value", e.name(), "label", e.getLabel()))
                .toList();

        List<Map<String, String>> encoterms = Arrays.stream(Encoterms.values())
                .map(e -> Map.of("value", e.name(), "label", e.getLabel()))
                .toList();

        return Map.of(
                "containerTypes", containerTypes,
                "freightTypes", freightTypes,
                "encoterms", encoterms
        );
    }
}