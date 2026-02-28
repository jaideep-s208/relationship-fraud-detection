package com.example.relationshipsystem.repository;

import com.example.relationshipsystem.model.Transaction;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import java.util.List;

public interface TransactionRepository
        extends Neo4jRepository<Transaction, String> {
    @Query("""
MATCH (t:Transaction)
WHERE t.deviceId = $deviceId
RETURN t
""")
    List<Transaction> findByDeviceId(String deviceId);

    @Query("""
MATCH (t:Transaction)
WHERE t.ipAddress = $ipAddress
RETURN t
""")
    List<Transaction> findByIpAddress(String ipAddress);
}