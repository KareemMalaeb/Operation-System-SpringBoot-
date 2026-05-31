package com.example.OperationSystem.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.OperationSystem.service.EnumService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/enums")
@RequiredArgsConstructor
public class EnumController {

    private final EnumService enumService;

    @GetMapping
    public ResponseEntity<Map<String, List<Map<String, String>>>> getEnums() {
        return ResponseEntity.ok(enumService.getEnums());
    }
}
