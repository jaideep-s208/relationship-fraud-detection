package com.example.relationshipsystem.controller;

import org.springframework.web.bind.annotation.*;
import com.example.relationshipsystem.service.RelationshipService;
import com.example.relationshipsystem.model.GraphResponse;
import java.util.List;
import java.util.Map;
@CrossOrigin("*")
@RestController
@RequestMapping("/api/relationships")
public class RelationshipController {

    private final RelationshipService service;

    public RelationshipController(RelationshipService service) {
        this.service = service;
    }

    @GetMapping("/user/{id}")
    public GraphResponse getUserRelations(@PathVariable String id) {
        return service.getUserRelationships(id);
    }

    @GetMapping("/transaction/{id}")
    public List<Map<String, Object>> getTransactionRelations(@PathVariable String id) {
        return service.getTransactionRelationships(id);
    }

    @GetMapping("/suspicious")
    public List<Map<String, Object>> suspiciousUsers() {
        return service.getSuspiciousUsers();
    }
    @GetMapping("/user/{id}/depth/{depth}")
    public GraphResponse getUserDepth(
            @PathVariable String id,
            @PathVariable int depth) {

        return service.getUserGraphDepth(id, depth);
    }
    @GetMapping("/suspicious/{id}")
    public GraphResponse suspicious(@PathVariable String id) {
        return service.getSuspiciousNetwork(id);
    }
    @GetMapping("/shortest/{u1}/{u2}")
    public GraphResponse shortest(
            @PathVariable String u1,
            @PathVariable String u2){
        return service.shortestPath(u1,u2);
    }

}