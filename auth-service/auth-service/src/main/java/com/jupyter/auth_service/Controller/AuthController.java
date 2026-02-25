package com.jupyter.auth_service.Controller;

import com.jupyter.auth_service.Service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @GetMapping("/login")

    @PostMapping("/login")
    public Map<String, String> login(@RequestParam String username) {
        String token = jwtService.generateToken(username);
        return Map.of("token", token);
    }
}