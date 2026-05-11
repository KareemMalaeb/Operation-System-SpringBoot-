package com.example.OperationSystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class HelloController {

@GetMapping("/public/hello")
public String hello() {
    return "Hello, World! this is a public endpoint.";
}

@GetMapping("/private/hello")
public String helloPrivate() {
    return "Hello, World! this is a private endpoint.";
}
}
