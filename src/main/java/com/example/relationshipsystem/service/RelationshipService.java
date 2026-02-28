package com.example.relationshipsystem.service;

import org.springframework.stereotype.Service;
import org.springframework.data.neo4j.core.Neo4jClient;
import com.example.relationshipsystem.model.GraphResponse;
import java.util.*;
import java.util.Map;

@Service
public class RelationshipService {

    private final Neo4jClient neo4jClient;

    public RelationshipService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    // ===============================
    // USER RELATIONSHIPS
    // ===============================
    public GraphResponse getUserRelationships(String id) {

        String query = """
        MATCH (u:User {id:$id})-[r]->(n)
        RETURN u.id AS source,
               type(r) AS relation,
               properties(n) AS nodeProps,
               labels(n) AS labels
    """;

        Collection<Map<String,Object>> results =
                neo4jClient.query(query)
                        .bind(id).to("id")
                        .fetch()
                        .all();

        List<Map<String,Object>> nodes = new ArrayList<>();
        List<Map<String,Object>> links = new ArrayList<>();

        // add main user node
        Map<String,Object> root = new HashMap<>();
        root.put("id", id);
        root.put("label", "User");
        nodes.add(root);

        for (Map<String,Object> row : results) {

            Map<String,Object> props =
                    (Map<String,Object>) row.get("nodeProps");

            String targetId = (String) props.get("id");

            Map<String,Object> node = new HashMap<>();
            node.put("id", targetId);
            node.put("label", ((List<?>)row.get("labels")).get(0));
            node.put("properties", props);

            nodes.add(node);

            Map<String,Object> link = new HashMap<>();
            link.put("source", row.get("source"));
            link.put("target", targetId);
            link.put("type", row.get("relation"));

            links.add(link);
        }

        GraphResponse response = new GraphResponse();
        response.setNodes(nodes);
        response.setLinks(links);

        return response;
    }
    // ===============================
    // TRANSACTION RELATIONSHIPS
    // ===============================
    public List<Map<String, Object>> getTransactionRelationships(String id) {

        String query = """
        MATCH (t:Transaction {id:$id})-[r]-(n)
        RETURN t.id AS transactionId,
               type(r) AS relation,
               n
        """;

        return new ArrayList<>(
                neo4jClient.query(query)
                        .bind(id).to("id")
                        .fetch()
                        .all()
        );

    }
    public List<Map<String, Object>> getSuspiciousUsers() {

        String query = """
        MATCH (u1:User)-[:PARTICIPATED_IN]->(t:Transaction)<-[:PARTICIPATED_IN]-(u2:User)
        WHERE u1.id <> u2.id
        AND t.deviceId IS NOT NULL
        RETURN DISTINCT
            u1.id AS user1,
            u2.id AS user2,
            t.deviceId AS device
    """;

        return neo4jClient.query(query)
                .fetch()
                .all()
                .stream()
                .toList();
    }
    public GraphResponse getUserGraphDepth(String id, int depth) {

        String query = String.format("""
MATCH path = (u:User {id:$id})-[*1..%d]-(n)
WITH collect(DISTINCT nodes(path)) AS nodeLists,
     collect(DISTINCT relationships(path)) AS relLists
UNWIND nodeLists AS nl
UNWIND nl AS node
WITH DISTINCT node, relLists
UNWIND relLists AS rl
UNWIND rl AS rel
RETURN DISTINCT
    node.id AS nodeId,
    labels(node) AS labels,
    properties(node) AS props,
    startNode(rel).id AS source,
    endNode(rel).id AS target,
    type(rel) AS type
""", depth);
        Collection<Map<String,Object>> result =
                neo4jClient.query(query)
                        .bind(id).to("id")
                        .fetch()
                        .all();

        List<Map<String,Object>> nodes = new ArrayList<>();
        List<Map<String,Object>> links = new ArrayList<>();

        Set<String> addedNodes = new HashSet<>();
        Set<String> addedLinks = new HashSet<>();

        for (Map<String,Object> row : result) {

            /* ---------- NODES ---------- */
            String nodeId = (String) row.get("nodeId");

            if (nodeId != null && addedNodes.add(nodeId)) {

                Map<String,Object> node = new HashMap<>();
                node.put("id", nodeId);

                List<?> labels = (List<?>) row.get("labels");
                node.put("label",
                        (labels != null && !labels.isEmpty())
                                ? labels.get(0)
                                : "Node");

                node.put("properties", row.get("props"));

                nodes.add(node);
            }

            /* ---------- LINKS ---------- */
            String source = (String) row.get("source");
            String target = (String) row.get("target");
            String type   = (String) row.get("type");

            if (source != null && target != null && type != null) {

                String key = source + "-" + target + "-" + type;

                if (addedLinks.add(key)) {
                    Map<String,Object> link = new HashMap<>();
                    link.put("source", source);
                    link.put("target", target);
                    link.put("type", type);

                    links.add(link);
                }
            }
        }
        // ============================
// ADD SHARED DEVICE / IP LINKS
// ============================

        String suspiciousQuery = """
MATCH (u1:User)-[:PARTICIPATED_IN]->(t1:Transaction)
MATCH (u2:User)-[:PARTICIPATED_IN]->(t2:Transaction)
WHERE u1.id <> u2.id

WITH u1, u2, t1, t2

// same device
WHERE t1.deviceId IS NOT NULL
AND t1.deviceId = t2.deviceId
RETURN DISTINCT u1.id AS source,
                u2.id AS target,
                'SHARED_DEVICE' AS type
""";

        neo4jClient.query(suspiciousQuery)
                .fetch()
                .all()
                .forEach(row -> {
                    Map<String,Object> link = new HashMap<>();
                    link.put("source", row.get("source"));
                    link.put("target", row.get("target"));
                    link.put("type", row.get("type"));
                    links.add(link);
                });

        String ipQuery = """
MATCH (u1:User)-[:PARTICIPATED_IN]->(t1:Transaction)
MATCH (u2:User)-[:PARTICIPATED_IN]->(t2:Transaction)
WHERE u1.id <> u2.id
AND t1.ipAddress IS NOT NULL
AND t1.ipAddress = t2.ipAddress
RETURN DISTINCT u1.id AS source,
                u2.id AS target,
                'SHARED_IP' AS type
""";

        neo4jClient.query(ipQuery)
                .fetch()
                .all()
                .forEach(row -> {
                    Map<String,Object> link = new HashMap<>();
                    link.put("source", row.get("source"));
                    link.put("target", row.get("target"));
                    link.put("type", row.get("type"));
                    links.add(link);
                });
        GraphResponse response = new GraphResponse();
        response.setNodes(nodes);
        response.setLinks(links);

        return response;
    }
    public GraphResponse shortestPath(String user1, String user2) {

        String query = """
    MATCH path =
    shortestPath(
        (u1:User {id:$u1})-[*..6]-(u2:User {id:$u2})
    )

    UNWIND nodes(path) AS node
    UNWIND relationships(path) AS rel

    RETURN DISTINCT
        node.id AS nodeId,
        labels(node) AS labels,
        properties(node) AS props,
        startNode(rel).id AS source,
        endNode(rel).id AS target,
        type(rel) AS type
    """;

        Collection<Map<String,Object>> result =
                neo4jClient.query(query)
                        .bind(user1).to("u1")
                        .bind(user2).to("u2")
                        .fetch()
                        .all();

        List<Map<String,Object>> nodes = new ArrayList<>();
        List<Map<String,Object>> links = new ArrayList<>();

        Set<String> addedNodes = new HashSet<>();
        Set<String> addedLinks = new HashSet<>();

        for(Map<String,Object> row : result){

            String nodeId=(String)row.get("nodeId");

            if(addedNodes.add(nodeId)){
                Map<String,Object> n=new HashMap<>();
                n.put("id",nodeId);
                n.put("label",((List<?>)row.get("labels")).get(0));
                n.put("properties",row.get("props"));
                nodes.add(n);
            }

            String source=(String)row.get("source");
            String target=(String)row.get("target");
            String type=(String)row.get("type");

            if(source!=null && target!=null){
                String key=source+"-"+target+"-"+type;
                if(addedLinks.add(key)){
                    Map<String,Object> l=new HashMap<>();
                    l.put("source",source);
                    l.put("target",target);
                    l.put("type",type);
                    links.add(l);
                }
            }
        }

        GraphResponse res=new GraphResponse();
        res.setNodes(nodes);
        res.setLinks(links);
        return res;
    }

    public GraphResponse getSuspiciousNetwork(String userId) {

        String query = """
        MATCH (u:User {id:$id})-[:PARTICIPATED_IN]->(t:Transaction)

        // same device
        OPTIONAL MATCH (t)<-[:PARTICIPATED_IN]-(other:User)
        WHERE t.deviceId IS NOT NULL

        // same IP
        OPTIONAL MATCH (t2:Transaction)
        WHERE t2.ipAddress = t.ipAddress
        MATCH (ipUser:User)-[:PARTICIPATED_IN]->(t2)

        WITH collect(DISTINCT other) +
             collect(DISTINCT ipUser) AS suspiciousUsers,
             collect(DISTINCT t) +
             collect(DISTINCT t2) AS transactions

        UNWIND suspiciousUsers + transactions AS node
        RETURN DISTINCT
            elementId(node) AS nodeId,
            labels(node) AS labels,
            properties(node) AS props
        """;

        Collection<Map<String,Object>> result =
                neo4jClient.query(query)
                        .bind(userId).to("id")
                        .fetch()
                        .all();

        List<Map<String,Object>> nodes = new ArrayList<>();

        for (Map<String,Object> row : result) {

            Map<String,Object> node = new HashMap<>();
            node.put("id", row.get("nodeId"));

            List<?> labels = (List<?>) row.get("labels");
            node.put("label",
                    labels != null && !labels.isEmpty()
                            ? labels.get(0)
                            : "Node");

            node.put("properties", row.get("props"));

            nodes.add(node);
        }

        GraphResponse response = new GraphResponse();
        response.setNodes(nodes);
        response.setLinks(new ArrayList<>());

        return response;
    }
}