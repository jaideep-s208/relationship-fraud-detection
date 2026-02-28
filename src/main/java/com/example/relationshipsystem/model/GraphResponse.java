package com.example.relationshipsystem.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class GraphResponse {

    private List<Map<String, Object>> nodes;
    private List<Map<String, Object>> links;
}