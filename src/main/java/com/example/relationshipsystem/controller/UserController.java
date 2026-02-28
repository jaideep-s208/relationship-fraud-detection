package com.example.relationshipsystem.controller;

import com.example.relationshipsystem.model.User;
import com.example.relationshipsystem.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // GET ALL USERS
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepository.findAll();
    }
    // CREATE USER
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }
}