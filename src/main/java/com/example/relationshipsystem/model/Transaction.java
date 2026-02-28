package com.example.relationshipsystem.model;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.relationshipsystem.model.TransactionLink;
import java.util.ArrayList;
import java.util.List;

@Data
@Node("Transaction")
public class Transaction {

    @Id
    private String id;

    private double amount;
    private String fromUserId;
    private String toUserId;
    private String deviceId;
    private String ipAddress;

    @Relationship(type = "LINKED_TO")
    @JsonIgnore
    private List<TransactionLink> linkedTransactions = new ArrayList<>();
}