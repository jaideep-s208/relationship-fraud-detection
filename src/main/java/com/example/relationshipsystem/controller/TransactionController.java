package com.example.relationshipsystem.controller;

import com.example.relationshipsystem.model.Transaction;
import com.example.relationshipsystem.service.TransactionService;
import org.springframework.web.bind.annotation.*;
import com.example.relationshipsystem.repository.TransactionRepository;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@CrossOrigin("*")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    public Transaction create(@RequestBody Transaction transaction) {
        return service.save(transaction);
    }

    @GetMapping
    public List<Transaction> getAll() {
        return service.getAll();
    }

}