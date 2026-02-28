package com.example.relationshipsystem.service;

import com.example.relationshipsystem.model.User;
import com.example.relationshipsystem.repository.UserRepository;
import com.example.relationshipsystem.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}