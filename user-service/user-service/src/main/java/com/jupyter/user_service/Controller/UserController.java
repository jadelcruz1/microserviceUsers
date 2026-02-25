package com.jupyter.user_service.Controller;

import com.jupyter.user_service.Entity.User;
import com.jupyter.user_service.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository repository;

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_users.write')")
    public User create(@RequestBody User user) {
        return repository.save(user);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_users.read')")
    public User getById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_users.read')")
    public List<User> list() {
        return repository.findAll();
    }
}
