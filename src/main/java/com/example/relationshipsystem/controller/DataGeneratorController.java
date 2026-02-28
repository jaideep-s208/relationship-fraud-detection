package com.example.relationshipsystem.controller;

import com.example.relationshipsystem.service.DataGeneratorService;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/data")
public class DataGeneratorController {

    private final DataGeneratorService service;

    public DataGeneratorController(DataGeneratorService service) {
        this.service = service;
    }

    @RequestMapping(value = "/reset", method = {RequestMethod.GET, RequestMethod.POST})
    public String reset() {
        service.clearDatabase();
        return "Database cleared";
    }

    @RequestMapping(value = "/generate-demo", method = {RequestMethod.GET, RequestMethod.POST})
    public String generateDemo() {
        service.generateDemoData();
        return "Demo data created";
    }

    @RequestMapping(value = "/generate-large", method = {RequestMethod.GET, RequestMethod.POST})
    public String generateLarge() {
        service.generateLargeData(100000);
        return "Large dataset generated";
    }

    @RequestMapping(value = "/generate-users", method = {RequestMethod.GET, RequestMethod.POST})
    public String generateUsers() {
        service.generateUsersIfEmpty(200);
        return "Users Created";
    }
}
