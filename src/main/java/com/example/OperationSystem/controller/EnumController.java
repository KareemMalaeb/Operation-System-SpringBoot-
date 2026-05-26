package com.example.OperationSystem.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.OperationSystem.enums.ContainerType;
import com.example.OperationSystem.enums.Encoterms;
import com.example.OperationSystem.enums.FreightType;

@RestController
@RequestMapping("/api/enums")
public class EnumController {

    @GetMapping
    public ResponseEntity<Map<String, List<Map<String, String>>>> getEnums() {
        List<Map<String, String>> containerTypes = Arrays.stream(ContainerType.values())
                .map(e -> (Map<String, String>) Map.of("value", e.name(), "label", e.getLabel()))
                .toList();

        List<Map<String, String>> freightTypes = Arrays.stream(FreightType.values())
                .map(e -> (Map<String, String>) Map.of("value", e.name(), "label", e.getLabel()))
                .toList();

        List<Map<String, String>> encoterms = Arrays.stream(Encoterms.values())
                .map(e -> (Map<String, String>) Map.of("value", e.name(), "label", e.getLabel()))
                .toList();

        return ResponseEntity.ok(Map.of(
                "containerTypes", containerTypes,
                "freightTypes", freightTypes,
                "encoterms", encoterms
                    
        ));
    }
}